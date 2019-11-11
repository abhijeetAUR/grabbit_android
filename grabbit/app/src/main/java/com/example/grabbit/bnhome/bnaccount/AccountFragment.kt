package com.example.grabbit.bnhome.bnaccount


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.grabbit.R
import com.example.grabbit.login.LoginActivity
import com.example.grabbit.utils.PREF_NAME
import com.example.grabbit.utils.PRIVATE_MODE
import com.example.grabbit.utils.username
import kotlinx.android.synthetic.main.fragment_account.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {
    var sharedPreferences: SharedPreferences? = null

    interface BtnLogoutClicked{
        fun logoutApplication()
    }

    var btnLogoutClicked : BtnLogoutClicked?= null

    fun setOnClickListenerBtnLogoutClicked(listener: BtnLogoutClicked){
        btnLogoutClicked = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val name = sharedPreferences!!.getString(username, "User")

        username_display.text = name

        logout.setOnClickListener {
            if (btnLogoutClicked != null){
                btnLogoutClicked!!.logoutApplication()
            }
        }
    }


}
