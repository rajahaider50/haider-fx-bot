package com.family.safety.platform.by.raja.haider.ali.ui.home

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
import com.family.safety.platform.by.raja.haider.ali.repository.AuthRepository
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.viewmodel.DeviceViewModel

class HomeFragment : Fragment() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var prefManager: PreferenceManager
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())
        deviceViewModel = ViewModelProvider(requireActivity())[DeviceViewModel::class.java]

        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val rvDevices = view.findViewById<RecyclerView>(R.id.rvDevices)
        val tvNoDevices = view.findViewById<TextView>(R.id.tvNoDevices)
        val tvTotalDevices = view.findViewById<TextView>(R.id.tvTotalDevices)
        val tvOnlineCount = view.findViewById<TextView>(R.id.tvOnlineCount)
        val tvAlertCount = view.findViewById<TextView>(R.id.tvAlertCount)
        val tvLocationCount = view.findViewById<TextView>(R.id.tvLocationCount)

        val uid = prefManager.userId
        if (uid.isNotEmpty()) {
            deviceViewModel.loadDevices(uid)
        }

        deviceAdapter = DeviceAdapter { device ->
            // Navigate to device detail
        }

        rvDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceAdapter
        }

        deviceViewModel.devices.observe(viewLifecycleOwner) { devices ->
            deviceAdapter.submitList(devices)
            tvTotalDevices.text = devices.size.toString()
            tvOnlineCount.text = devices.count { it.isOnline }.toString()
            tvNoDevices.visibility = if (devices.isEmpty()) View.VISIBLE else View.GONE
            rvDevices.visibility = if (devices.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }
}

class DeviceAdapter(
    private val onClick: (Device) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Device, DeviceAdapter.ViewHolder>(
    object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(old: Device, new: Device) = old.deviceId == new.deviceId
        override fun areContentsTheSame(old: Device, new: Device) = old == new
    }
) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDeviceName: TextView = view.findViewById(R.id.tvDeviceName)
        val tvDeviceModel: TextView = view.findViewById(R.id.tvDeviceModel)
        val tvBattery: TextView = view.findViewById(R.id.tvBattery)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val viewStatusDot: View = view.findViewById(R.id.viewStatusDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.tvDeviceName.text = device.deviceName.ifEmpty { device.deviceModel }
        holder.tvDeviceModel.text = device.deviceModel
        holder.tvBattery.text = "${device.batteryLevel}%"
        holder.tvStatus.text = if (device.isOnline) "Online" else "Offline"
        holder.viewStatusDot.setBackgroundResource(
            if (device.isOnline) R.drawable.status_online else R.drawable.status_offline
        )
        holder.itemView.setOnClickListener { onClick(device) }
    }
}
