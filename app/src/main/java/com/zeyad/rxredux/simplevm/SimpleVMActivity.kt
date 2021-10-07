package com.zeyad.rxredux.simplevm

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.vm.InputStrategy
import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Error
import com.zeyad.rxredux.core.vm.rxvm.Progress
import com.zeyad.rxredux.core.vm.rxvm.State
import kotlinx.android.synthetic.main.activity_modern.changeBackgroundButton
import kotlinx.android.synthetic.main.activity_modern.container
import kotlinx.android.synthetic.main.activity_modern.errorText
import kotlinx.android.synthetic.main.activity_modern.progressBar
import kotlinx.android.synthetic.main.activity_modern.showDialogButton
import kotlinx.android.synthetic.main.activity_modern.showErrorButton

class SimpleVMActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modern)

        val viewModel = ViewModelProvider(this, MySimpleViewModelFactory(this, savedInstanceState))
                .get(MySimpleVm::class.java)
        viewModel.bind(ColorBackgroundState(Color.BLUE))

        changeBackgroundButton.setOnClickListener {
            viewModel.offer(ChangeBackgroundButtonClickInput(), InputStrategy.THROTTLE)
        }

        showDialogButton.setOnClickListener {
            viewModel.offer(ShowDialogButtonClickInput)
        }

        showErrorButton.setOnClickListener {
            viewModel.offer(ErrorInput)
        }

        viewModel.observe(this) {
            states { bindState(it) }
            effects { bindEffect(it) }
            errors { bindError(it) }
            progress { toggleProgress(it) }
        }
    }

    private fun toggleProgress(progress: Progress) {
        if (progress.input is ChangeBackgroundButtonClickInput) {
            if (progress.isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun bindError(error: Error) {
        errorText.text = error.message
    }

    private fun bindEffect(effect: Effect) {
        when (effect) {
            is ShowDialogEffect -> {
                AlertDialog.Builder(this)
                        .setTitle("Dialog")
                        .setMessage("Dialog effect!")
                        .create().show()
            }
        }
    }

    private fun bindState(state: State) {
        if (state is ColorBackgroundState) {
            container.setBackgroundColor(state.color)
        }
    }
}
