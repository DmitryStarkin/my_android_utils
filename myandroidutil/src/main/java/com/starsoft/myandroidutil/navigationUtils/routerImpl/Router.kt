/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  //www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an «AS IS» BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starsoft.myandroidutil.navigationUtils.routerImpl

import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import com.starsoft.myandroidutil.R
import com.starsoft.myandroidutil.navigationUtils.enums.BackstackBehavior
import com.starsoft.myandroidutil.navigationUtils.enums.ReplaceBehavior
import com.starsoft.myandroidutil.navigationUtils.enums.RoutPolicy
import com.starsoft.myandroidutil.navigationUtils.interfaces.Host
import com.starsoft.myandroidutil.navigationUtils.interfaces.Rout
import com.starsoft.myandroidutil.refutils.isInstanceOrExtend

/**
 * Created by Dmitry Starkin on 06.02.2022 16:30.
 */
open class Router(private val host: Host, private val config: RouterConfig = RouterConfig()) {

    constructor(activity: FragmentActivity, config: RouterConfig = RouterConfig()): this(HostStub(hostActivity = activity), config)

    constructor(fragment: Fragment, config: RouterConfig = RouterConfig()): this(HostStub(hostFragment = fragment), config)

    private val manager = getFragmentManager(host)

    open val myRouts: List<Rout> = emptyList()

    companion object {
        private const val REPLACE_FLAG_KEY = "replaceOrFinish"
        private const val BACK_STACK_FLAG_KEY = "addToBackStack"
        private const val IGNORE_ROUT_POLICY_FLAG_KEY = "ignoreRoutPolicy"
        private const val ENTER_ANIMATION_KEY = "routerEnterAnimation"
        private const val EXIT_ANIMATION_KEY = "routerExitAnimation"
        private const val TRANSITION_KEY = "routerTransition"

        fun Bundle?.addReplaceFlag(replaceBehavior: ReplaceBehavior): Bundle =
            this?.let {
                it.putBoolean(REPLACE_FLAG_KEY, replaceBehavior.behavior)
                it
            } ?: replaceBehavior.packToBundle()

        fun ReplaceBehavior.packToBundle(): Bundle =
            bundleOf(
                REPLACE_FLAG_KEY to this.behavior
            )

        fun Bundle?.addBackStackBehavior(backstackBehavior: BackstackBehavior): Bundle =
            this?.let {
                it.putBoolean(BACK_STACK_FLAG_KEY, backstackBehavior.behavior)
                it
            } ?: backstackBehavior.packToBundle()

        fun BackstackBehavior.packToBundle(): Bundle =
            bundleOf(
                BACK_STACK_FLAG_KEY to this.behavior
            )

        fun Bundle?.addAnimation(@AnimatorRes @AnimRes enter: Int,
                                 @AnimatorRes @AnimRes exit: Int): Bundle =
            this?.let {
                it.putInt(ENTER_ANIMATION_KEY, enter)
                it.putInt(EXIT_ANIMATION_KEY, exit)
                it
            } ?: packAnimationToBundle(enter, exit)

        fun packAnimationToBundle(@AnimatorRes @AnimRes enter: Int,
                                  @AnimatorRes @AnimRes exit: Int): Bundle =
            bundleOf(
                ENTER_ANIMATION_KEY to enter,
                EXIT_ANIMATION_KEY to exit
            )

        fun Bundle?.addTransition(transition: Int): Bundle =
            this?.let {
                it.putInt(TRANSITION_KEY, transition)
                it
            } ?: packTransitionToBundle(transition)

        fun packTransitionToBundle(transition: Int): Bundle =
            bundleOf(
                TRANSITION_KEY to transition
            )

        fun Bundle?.addIgnoreRoutPolicyFlag(flag: Boolean): Bundle =
            this?.let {
                it.putBoolean(IGNORE_ROUT_POLICY_FLAG_KEY, flag)
                it
            } ?: packIgnoreRoutPolicyFlagToBundle(flag)

        fun packIgnoreRoutPolicyFlagToBundle(flag: Boolean): Bundle =
            bundleOf(
                IGNORE_ROUT_POLICY_FLAG_KEY to flag
            )

        private fun Bundle?.getTransition(): Int? =
            if(this == null || !this.containsKey(TRANSITION_KEY)){
                null
            } else if(this.getInt(TRANSITION_KEY) !in supportedTransitions){
                null
            } else {
                this.getInt(TRANSITION_KEY)
            }

        private fun Bundle?.getEnterAnimation(): Int =
            this?.getInt(ENTER_ANIMATION_KEY, 0) ?: 0

        private fun Bundle?.getExitAnimation(): Int =
            this?.getInt(EXIT_ANIMATION_KEY, 0) ?: 0

        private fun Bundle?.getReplaceFlag(defValue: ReplaceBehavior): Boolean =
            this?.getBoolean(REPLACE_FLAG_KEY, defValue.behavior) ?: defValue.behavior

        private fun Bundle?.getBackStackFlag(defValue: BackstackBehavior): Boolean =
            this?.getBoolean(BACK_STACK_FLAG_KEY, defValue.behavior) ?: defValue.behavior

        private fun Bundle?.getIgnoreRoutPolicyFlag(): Boolean =
            this?.getBoolean(IGNORE_ROUT_POLICY_FLAG_KEY, false) ?: false

        private val supportedTransitions = listOf(
            FragmentTransaction.TRANSIT_NONE,
            FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE,
            FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }

    private val entries: ArrayList<Entry> = ArrayList()

    private var curEntry: Entry? = null
        set(value) {
            if (field?.data.getBackStackFlag(config.defaultBackstackBehavior)) {
                field?.let { entries.add(it) }
            }
            field = value
        }

    val currentDestination = curEntry?.rout?.destination

    fun moveTo(rout: Rout, data: Bundle? = null) {
        val unionData = rout.data?.let {
            it.putAll(data)
            it
        } ?: data
        if (!config.routPolicy.allow && rout !in myRouts && !data.getIgnoreRoutPolicyFlag()) throw Exception("Wrong rout destination")
        when {
            rout.destination.isInstanceOrExtend(Fragment::class.java) -> {
                moveToFragment(rout, unionData)?.apply { curEntry = this }
            }
            rout.destination.isInstanceOrExtend(Activity::class.java) -> {
                moveToActivity(rout, unionData, unionData.getReplaceFlag(config.defaultReplaceBehavior))
                curEntry = Entry(rout, unionData.addBackStackBehavior(BackstackBehavior.NotAdd))
            }
            rout.destination.isInstanceOrExtend(Service::class.java) -> {
                startService(rout, unionData, unionData.getReplaceFlag(config.defaultReplaceBehavior))
                curEntry = Entry(rout, unionData.addBackStackBehavior(BackstackBehavior.NotAdd))
            }
            rout is OpenLink -> {
                openWebLink(rout.link)
            }
            else -> {
                throw Exception("Wrong rout destination")
            }
        }
    }

    fun moveBack() {
        //TODO
    }

    fun getAllFragments(): List<Fragment> {
        TODO()
    }

    private fun moveToActivity(rout: Rout, data: Bundle?, needFinish: Boolean) {
        val activity = getActivity(host)
        activity?.apply {
            if(activity.javaClass == rout.destination) return
            startActivity(Intent(this, rout.destination).apply { data?.apply { putExtras(this) } })
        }
        if (needFinish) {
            activity?.finish()
        }
    }

    private fun startService(rout: Rout, data: Bundle?, needFinish: Boolean) {
        val activity = getActivity(host)
        activity?.apply {
            startService(Intent(this, rout.destination).apply { data?.apply { putExtras(this) } })
        }
        if (needFinish) {
            activity?.finish()
        }
    }

    private fun openWebLink(
        link: String,
        @StringRes chooserTextId: Int = R.string.select_app_to_open_link
    ) {
        val activity = getActivity(host)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // needed for call Activity using context outside other Activity
        activity?.packageManager?.apply {
            intent.resolveActivity(this)?.apply {
                try {
                    activity.startActivity(intent);
                } catch (e: ActivityNotFoundException) {
                    activity.startActivity(
                        Intent.createChooser(
                            intent,
                            activity.getString(chooserTextId)
                        )
                    )
                }
            }
        }
    }

    private fun moveToFragment(rout: Rout, data: Bundle?): Entry? {
        if (data.getReplaceFlag(config.defaultReplaceBehavior)) {
            val fragment = rout.destination.newInstance() as Fragment
            if (showAsDialog(fragment, rout.destination.name) || host.getContainerId() == View.NO_ID) return null
            switchFragment(fragment.apply {
                arguments = data
            }, rout.destination.name)
            return Entry(rout, data)
        }
        manager.findFragmentByTag(rout.destination.name)?.also {
            it.arguments = data
            showFragment(it)
        } ?: run {
            val fragment = rout.destination.newInstance() as Fragment
            if (showAsDialog(fragment, rout.destination.name) || host.getContainerId() == View.NO_ID) return null
            setFragment(
                fragment.apply { arguments = data },
                rout.destination.name
            )
        }
        return Entry(rout, data)
    }

    private fun switchFragment(fragment: Fragment, tag: String) {
        manager.beginTransaction()
            .setAnimations(fragment.arguments)
            .replace(host.getContainerId(), fragment, tag)
        .commit()
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        manager.beginTransaction()
            .hideAllFragments(emptyList())
            .setAnimations(fragment.arguments)
            .add(host.getContainerId(), fragment, tag)
            .commit()
    }

    private fun showFragment(fragment: Fragment) {
        manager.beginTransaction()
            .hideAllFragments(listOf(fragment))
            .setAnimations(fragment.arguments)
            .show(fragment)
            .commit()
    }

    private fun FragmentTransaction.setAnimations(data: Bundle?):FragmentTransaction{
        data?.getTransition()?.let{
            setTransition(it)
        } ?: setCustomAnimations(data.getEnterAnimation(), data.getExitAnimation())
        return this
    }

    private fun showAsDialog(fragment: Fragment, tag: String): Boolean =
        if (fragment.javaClass.isInstanceOrExtend(DialogFragment::class.java)) {
            manager.let{
                (fragment as DialogFragment).show(it, tag)
                true
            } ?: false
        } else {
            false
        }

    private fun hideAll() {
        val transaction = manager.beginTransaction()
        manager.fragments.forEach {
            transaction.hide(it)
        }
        transaction.commit()
    }


    private fun FragmentManager.getAllFragments(exclude: List<Fragment>): List<Fragment> =
        this.fragments.mapNotNull {
            if (it !in exclude) {
                it
            } else {
                null
            }
        }

    private fun FragmentTransaction.hideAllFragments(exclude: List<Fragment>): FragmentTransaction {
        manager.getAllFragments(exclude).forEach {
            this.hide(it)
        }
        return this
    }

    private fun getFragmentManager(host: Host): FragmentManager {

        return when {
            host.javaClass.isInstanceOrExtend(Fragment::class.java) -> {
                (host as Fragment).childFragmentManager
            }
            host.javaClass.isInstanceOrExtend(FragmentActivity::class.java) -> {
                (host as FragmentActivity).supportFragmentManager
            }
            host is HostStub && host.hostActivity != null -> {
                host.hostActivity.supportFragmentManager
            }
            host is HostStub && host.hostFragment != null -> {
                host.hostFragment.childFragmentManager
            }
            else -> {
                throw Exception("wrong Host")
            }
        }

    }

    private fun getActivity(host: Host): Activity? {

        return when {
            host.javaClass.isInstanceOrExtend(Fragment::class.java) -> {
                (host as Fragment).activity
            }
            host.javaClass.isInstanceOrExtend(Activity::class.java) -> {
                (host as Activity)
            }
            host is HostStub && host.hostActivity != null -> {
                host.hostActivity
            }
            host is HostStub && host.hostFragment != null -> {
                host.hostFragment.activity
            }
            else -> {
                throw Exception("Wrong Host")
            }
        }
    }

    data class OpenLink
        (val link: String) : Rout {
        override val destination: Class<*>
            get() = OpenLink::class.java
    }

    private class HostStub(val hostFragment: Fragment? = null, val hostActivity: FragmentActivity? = null):
        Host {
        override fun getContainerId(): Int = View.NO_ID
    }

    private data class Entry(
        val rout: Rout,
        val data: Bundle?
    )

    data class RouterConfig(
        val defaultReplaceBehavior: ReplaceBehavior = ReplaceBehavior.Replace,
        val defaultBackstackBehavior: BackstackBehavior = BackstackBehavior.NotAdd,
        val routPolicy: RoutPolicy = RoutPolicy.AllowAll
    )
}