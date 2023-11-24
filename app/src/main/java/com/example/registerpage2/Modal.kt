package com.example.registerpage2

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            dismiss()
        }
        val start = view.findViewById<TextView>(R.id.startPrice)
        val end = view.findViewById<TextView>(R.id.endPrice)

        view.findViewById<Button>(R.id.btn_event_on).setOnClickListener {
            val bundleSet = Bundle()
            if(start.text.toString() != "" && end.text.toString()!="") {
                bundleSet.putInt("start", start.text.toString().toInt() ?: 0)
                bundleSet.putInt("end", end.text.toString().toInt() ?: 0)
                requireActivity().supportFragmentManager.setFragmentResult("MODAL", bundleSet)
            }
            dismiss()
        }

        view.findViewById<Button>(R.id.btn_reset).setOnClickListener {
                val bundleSet = Bundle()
                requireActivity().supportFragmentManager.setFragmentResult("RESET", bundleSet)

            dismiss()
        }
    }


    companion object {
        const val TAG = "BasicBottomModalSheet"
    }
}