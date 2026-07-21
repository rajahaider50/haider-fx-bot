package com.family.safety.platform.by.raja.haider.ali.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.family.safety.platform.by.raja.haider.ali.R
import com.family.safety.platform.by.raja.haider.ali.storage.PreferenceManager
import com.family.safety.platform.by.raja.haider.ali.viewmodel.LocationViewModel

class LocationFragment : Fragment() {

    private lateinit var locationViewModel: LocationViewModel
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PreferenceManager(requireContext())
        locationViewModel = ViewModelProvider(requireActivity())[LocationViewModel::class.java]

        val tvCurrentAddress = view.findViewById<TextView>(R.id.tvCurrentAddress)
        val tvLastUpdate = view.findViewById<TextView>(R.id.tvLastUpdate)

        locationViewModel.currentLocation.observe(viewLifecycleOwner) { loc ->
            if (loc != null) {
                tvCurrentAddress.text = loc.address.ifEmpty { "${loc.latitude}, ${loc.longitude}" }
                tvLastUpdate.text = com.family.safety.platform.by.raja.haider.ali.util.DateUtils.timeAgo(loc.timestamp)
            }
        }
    }
}
