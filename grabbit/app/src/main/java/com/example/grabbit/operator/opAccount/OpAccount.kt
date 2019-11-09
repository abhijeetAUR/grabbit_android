package com.example.grabbit.operator.opAccount


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.grabbit.R
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.username
import kotlinx.android.synthetic.main.fragment_op_account.*

/**
 * A simple [Fragment] subclass.
 */
class OpAccount : Fragment() {
    interface BtnLogoutClicked{
        fun logoutApplication()
    }
    private var btnLogoutClicked : BtnLogoutClicked?= null
    var sharedPreferences: SharedPreferences? = null

    fun setOnClickListenerBtnLogoutClicked(listener: BtnLogoutClicked){
        btnLogoutClicked = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_op_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        setupOperatorView()
        txt_op_logout.setOnClickListener {
            if (btnLogoutClicked != null){
                btnLogoutClicked!!.logoutApplication()
            }
        }
    }

    private fun setupOperatorView() {
        val name = sharedPreferences!!.getString(username, "User")
        activity!!.runOnUiThread {
            txt_op_username_display.text = "Hi $name"
        }
    }


}
