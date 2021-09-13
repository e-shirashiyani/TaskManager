package com.example.taskmanagerkotlin.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.example.taskmanagerkotlin.R
import com.example.taskmanagerkotlin.data.model.User
import com.example.taskmanagerkotlin.data.repository.UserDBRepository
import com.example.taskmanagerkotlin.databinding.FragmentLoginBinding
import com.example.taskmanagerkotlin.view.activity.SignUpActivity
import com.example.taskmanagerkotlin.view.activity.TaskListActivity
import java.security.Policy.getInstance
import java.text.Collator.getInstance
import java.util.*
import java.util.Calendar.getInstance
import java.util.Currency.getInstance


private const val REQUEST_CODE_SIGN_UP = 0
private const val BUNDLE_KEY_USERNAME = "UserBundle"
private const val BUNDLE_KEY_PASSWORD = "passBundle"
class LoginFragment : Fragment() {

    private lateinit var user: String
    private lateinit var pass: String
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var mUserRepository: UserDBRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUserRepository = UserDBRepository.getInstance(activity!!)!!

            if (savedInstanceState != null) {
                user = savedInstanceState.getString(BUNDLE_KEY_USERNAME).toString()
                pass = savedInstanceState.getString(BUNDLE_KEY_PASSWORD).toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        fragmentLoginBinding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login, container, false
        )




        listeners()
        return fragmentLoginBinding.root;
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun listeners() {
        fragmentLoginBinding.btnLoginLogin.setOnClickListener(View.OnClickListener {
            fragmentLoginBinding.usernameFormLogin.isErrorEnabled = false
            fragmentLoginBinding.passwordFormLogin.isErrorEnabled = false
            if (validateInput()) {

                val intent: Intent = TaskListActivity.newIntent(
                    activity,
                    fragmentLoginBinding.usernameLogin.text.toString(),
                    fragmentLoginBinding.passwordLogin.text.toString()
                )
                startActivity(intent)
            }
        })
        fragmentLoginBinding.btnSignUpLogin.setOnClickListener(View.OnClickListener {
            val intent: Intent = SignUpActivity.newIntent(
                activity,
                fragmentLoginBinding.usernameLogin.text.toString(),
                fragmentLoginBinding.passwordLogin.text.toString()
            )
            startActivityForResult(intent, REQUEST_CODE_SIGN_UP)
        })
        /*fragmentLoginBinding.btnAdminLogin.setOnClickListener(View.OnClickListener {
            if (checkAdmin()) {
                val intent: Intent = AdminListActivity.newIntent(activity)
                startActivity(intent)
            }
        })*/
    }

    private fun checkAdmin(): Boolean {
        return if (fragmentLoginBinding.usernameLogin.text.toString()
                .equals("Admin", ignoreCase = true) &&
            fragmentLoginBinding.passwordLogin.text.toString() == "1234"
        ) {
            true
        } else {
            callToast(R.string.toast_admin)
            false
        }
    }

    private fun validateInput(): Boolean {
        val user: User = mUserRepository.getUser(
            Objects.requireNonNull(fragmentLoginBinding.usernameLogin.text).toString(),
            fragmentLoginBinding.passwordLogin.text.toString()
        )
        if (fragmentLoginBinding.usernameLogin.text.toString().trim { it <= ' ' }.isEmpty()
            && fragmentLoginBinding.passwordLogin.text.toString().trim { it <= ' ' }.isEmpty()
        ) {
            fragmentLoginBinding.usernameFormLogin.isErrorEnabled = true
            fragmentLoginBinding.usernameFormLogin.error = "Field cannot be empty!"
            fragmentLoginBinding.passwordFormLogin.isErrorEnabled = true
            fragmentLoginBinding.passwordFormLogin.error = "Field cannot be empty!"
            return false
        } else if (fragmentLoginBinding.usernameLogin.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            fragmentLoginBinding.usernameFormLogin.isErrorEnabled = true
            fragmentLoginBinding.usernameFormLogin.error = "Field cannot be empty!"
            return false
        } else if (fragmentLoginBinding.passwordLogin.text.toString().trim { it <= ' ' }
                .isEmpty()) {
            fragmentLoginBinding.passwordFormLogin.isErrorEnabled = true
            fragmentLoginBinding.passwordFormLogin.error = "Field cannot be empty!"
            return false
        }
        if (user == null) {
            callToast(R.string.toast_login)
            return false
        } else {
            val inputUsername: String? = user.mUsername
            val inputPassword: String? = user.mPassword
            if (fragmentLoginBinding.usernameLogin.text.toString() != inputUsername ||
                fragmentLoginBinding.passwordLogin.text.toString() != inputPassword
            ) {
                callToast(R.string.toast_login)
                return false
            }
        }
        fragmentLoginBinding.usernameFormLogin.isErrorEnabled = false
        fragmentLoginBinding.passwordFormLogin.isErrorEnabled = false
        return true
    }

    private fun callToast(stringId: Int) {
        val toast = Toast.makeText(activity, stringId, Toast.LENGTH_SHORT)
        toast.show()
    }
}

