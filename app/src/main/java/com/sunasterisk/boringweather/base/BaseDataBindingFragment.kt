package com.sunasterisk.boringweather.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseDataBindingFragment<out T : ViewDataBinding> : Fragment() {
    private var _binding: T? = null

    /**
     * Available after [Fragment.onCreateView] returns
     * and before [Fragment.onDestroyView]'s super is called
     */
    protected val binding: T
        get() = _binding ?: throw IllegalStateException(
            "binding is only valid between onCreateView and onDestroyView"
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called in [Fragment.onCreateView] to get the rootView of [binding]
     */
    abstract fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T
}
