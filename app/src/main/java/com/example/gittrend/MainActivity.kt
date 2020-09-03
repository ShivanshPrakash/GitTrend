package com.example.gittrend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gittrend.database.Repository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appViewModel by viewModels<AppViewModel>()
    private val repoList = mutableListOf<Repository>()

    private lateinit var repoAdapter: RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repoRecyclerView = findViewById<RecyclerView>(R.id.repo_recycler_view)
        repoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        repoAdapter = RepoAdapter(repoList)
        repoRecyclerView.adapter = repoAdapter

        appViewModel.refreshFromApi()
        appViewModel.repoListLiveData.observe(this) { newRepoList ->
            Log.d("repoCount", newRepoList.size.toString())
            repoList.clear()
            repoList.addAll(newRepoList)
            if (this::repoAdapter.isInitialized) repoAdapter.notifyDataSetChanged()
        }
    }
}