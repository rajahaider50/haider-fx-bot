package com.family.safety.platform.by.raja.haider.ali.ui.automation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.model.AutomationRule
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import android.widget.Switch

class AutomationFragment : Fragment() {

    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_automation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())

        val rvRules = view.findViewById<RecyclerView>(R.id.rvRules)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        rvRules.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<View>(R.id.btnAddRule).setOnClickListener {
            android.content.Intent(requireContext(), AddRuleActivity::class.java).let {
                startActivity(it)
            }
        }
    }
}
