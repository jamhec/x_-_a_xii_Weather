package com.example.weatherapp.ui.weekly


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.APIResponse.Daily
import com.example.weatherapp.databinding.FragmentWeeklyBinding
import com.example.weatherapp.log.BaseFragment
import com.example.weatherapp.log.DebugTree
import timber.log.Timber
import java.text.SimpleDateFormat


class WeeklyFragment : BaseFragment() {

    private var _binding: FragmentWeeklyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val weeklyList = ArrayList<Daily>()
    val adapter = WeeklyAdapter(weeklyList)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.plant(DebugTree())

        _binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        //Date Range
//        var dateNow = SimpleDateFormat("MMMM d").format(Date())
//
//        var localnow = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//        var localWeek = Date.from(localnow.plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant())
//        var dateWeekfromNow = SimpleDateFormat("MMMM d").format(localWeek)
//
//        binding.dateRangeLabel.text = dateNow +" - "+ dateWeekfromNow

        // Weekly Recycler View
        binding.futureRecycler.adapter = adapter
        binding.futureRecycler.layoutManager = LinearLayoutManager(activity)

        setFragmentResultListener("key_to_weekly"){key,result ->
            weeklyList.clear()
            weeklyList.addAll(result.get("daily") as List<Daily>)
            adapter.notifyDataSetChanged()

            //Date Range on top
            var dateNow = SimpleDateFormat("MMMM d").format(weeklyList.first().dt.toLong()*1000)
            var dateWeekfromNow = SimpleDateFormat("MMMM d").format(weeklyList.last().dt.toLong()*1000)

            binding.dateRangeLabel.text = dateNow +" - "+ dateWeekfromNow

        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}