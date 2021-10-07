package com.zeyad.rxredux

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zeyad.rxredux.simplevm.SimpleVMActivity
import kotlinx.android.synthetic.main.activity_vm_selection.rxVMButton
import kotlinx.android.synthetic.main.activity_vm_selection.simpleRxVMButton

class VMSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vm_selection)

        rxVMButton.setOnClickListener {
            startActivity(Intent(this, MyActivity::class.java))
        }

        simpleRxVMButton.setOnClickListener {
            startActivity(Intent(this, SimpleVMActivity::class.java))
        }
    }
}
