package com.family.safety.platform.by.raja.haider.ali.ui.devices

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.model.Device
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.ui.home.DeviceAdapter
import com.family.safety.platform.by.raja.haider.ali.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton

class DevicesFragment : Fragment() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())
        deviceViewModel = ViewModelProvider(requireActivity())[DeviceViewModel::class.java]

        val rvDevices = view.findViewById<RecyclerView>(R.id.rvDevices)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)
        val btnPair = view.findViewById<MaterialButton>(R.id.btnPair)

        val adapter = DeviceAdapter { device ->
            val intent = Intent(requireContext(), DeviceDetailActivity::class.java)
            intent.putExtra("device_id", device.deviceId)
            startActivity(intent)
        }

        rvDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        val uid = prefManager.userId
        if (uid.isNotEmpty()) {
            deviceViewModel.loadDevices(uid)
        }

        deviceViewModel.devices.observe(viewLifecycleOwner) { devices ->
            adapter.submitList(devices)
            tvEmpty.visibility = if (devices.isEmpty()) View.VISIBLE else View.GONE
            rvDevices.visibility = if (devices.isNotEmpty()) View.VISIBLE else View.GONE
        }

        btnPair.setOnClickListener {
            startActivity(Intent(requireContext(), PairDeviceActivity::class.java))
        }
    }
}
