package com.example.umc.Quote.Sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.R
import com.example.umc.model.RankingItem

class TabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.fv_ranking)
        val position = arguments?.getInt("position") ?: 0

        val category = when (position) {
            0 -> "채소"
            1 -> "육류"
            2 -> "과일"
            3 -> "어류/수산물"
            else -> ""
        }


    }
}
