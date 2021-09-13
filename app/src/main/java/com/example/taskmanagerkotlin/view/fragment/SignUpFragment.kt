package com.example.taskmanagerkotlin.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.User
import com.example.taskmanagerkotlin.data.repository.UserDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentSignUpBinding
import java.util.*
import java.util.Calendar.getInstance

private const val ARG_USERNAME = "username"
private const val ARG_PASSWORD = "password"
private const val EXTRA_USERNAME_SIGN_UP = "extraUsername"
private const val EXTRA_PASSWORD_SIGN_UP = "EXTRA_password"

class SignUpFragment : Fragment() {
    private lateinit var mUserRepository: UserDBRepository
    private lateinit var fragmentSignUpBinding: FragmentSignUpBinding
    private lateinit var username :String
    private lateinit var password: String
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username=it.getString(ARG_USERNAME).toString()
            password=it.getString(ARG_PASSWORD).toString()

        }
        mUserRepository=UserDBRepository.getInstance(activity!!)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    fragmentSignUpBinding =DataBindingUtil.inflate(inflater,
        R.layout.fragment_sign_up,container,false)
    initView()
    listener()
    return fragmentSignUpBinding.root
    }

    private fun listener() {
       fragmentSignUpBinding.btnSignUpSignUP.setOnClickListener(View.OnClickListener {
           fragmentSignUpBinding.usernameFormSignUp.isErrorEnabled = false
           fragmentSignUpBinding.passwordFormSignUp.isErrorEnabled = false
           if (validInput())
               setUserPassResult()
           activity!!.finish()

       })
    }

    private fun setUserPassResult() {
        val username: String = Objects.requireNonNull(fragmentSignUpBinding.usernameSignUp.text).toString()
        val password: String = Objects.requireNonNull(fragmentSignUpBinding.passwordSignUp.text).toString()
        val user = User(username, password)
        mUserRepository.insertUser(user)
        val intent = Intent()
        intent.putExtra(
            EXTRA_USERNAME_SIGN_UP,
            fragmentSignUpBinding.usernameSignUp.text.toString()
        )
        intent.putExtra(
            EXTRA_PASSWORD_SIGN_UP,
            fragmentSignUpBinding.passwordSignUp.text.toString()
        )
        activity!!.setResult(Activity.RESULT_OK, intent)
    }

    private fun validInput(): Boolean {
        if (fragmentSignUpBinding.usernameSignUp.text.toString().trim { it <= ' ' }.isEmpty()
            && fragmentSignUpBinding.passwordSignUp.text.toString().trim { it <= ' ' }.isEmpty()
        ) {
            fragmentSignUpBinding.usernameFormSignUp.isErrorEnabled = true
            fragmentSignUpBinding.usernameFormSignUp.error = "Field cannot be empty!"
            fragmentSignUpBinding.passwordFormSignUp.isErrorEnabled = true
            fragmentSignUpBinding.passwordFormSignUp.error = "Field cannot be empty!"
            return false
        } else if (fragmentSignUpBinding.usernameSignUp.text.toString().trim { it <= ' ' }.isEmpty()) {
            fragmentSignUpBinding.usernameFormSignUp.isErrorEnabled = true
            fragmentSignUpBinding.usernameFormSignUp.error = "Field cannot be empty!"
            return false
        } else if (fragmentSignUpBinding.passwordSignUp.text.toString().trim { it <= ' ' }.isEmpty()) {
            fragmentSignUpBinding.passwordFormSignUp.isErrorEnabled = true
            fragmentSignUpBinding.passwordFormSignUp.error = "Field cannot be empty!"
            return false
        }
        fragmentSignUpBinding.usernameFormSignUp.isErrorEnabled = false
        fragmentSignUpBinding.passwordFormSignUp.isErrorEnabled = false
        return true
    }

    private fun initView() {
        fragmentSignUpBinding.usernameSignUp.setText(username)
        fragmentSignUpBinding.passwordSignUp.setText(password)
    }

    companion object {

        @JvmStatic
        fun newInstance(username: String , password:String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME,username)
                    putString(ARG_PASSWORD,password)

                }
            }
    }
}