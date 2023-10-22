package com.example.mymoviesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mymoviesapp.R
import com.example.mymoviesapp.activities.MainActivity
import com.example.mymoviesapp.activities.MoviesDetailsActivity
import com.example.mymoviesapp.adapter.favorites.FavoritesAdapter
import com.example.mymoviesapp.adapter.favorites.FavoritesViewHolder
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.FragmentFavoritesBinding
import com.example.mymoviesapp.interfaces.MovieAction
import com.example.mymoviesapp.interfaces.MovieAction.AddToFavorite
import com.example.mymoviesapp.interfaces.MovieAction.ShowMovieDetails
import com.example.mymoviesapp.interfaces.MovieConstants.DETAILS_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.show
import com.example.mymoviesapp.room.FavoriteMovie
import com.example.mymoviesapp.room.FavoriteMovieDAO
import com.example.mymoviesapp.room.FavoriteMovieRoom
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FavoritesAdapter
    private lateinit var roomDAO: FavoriteMovieDAO

    private lateinit var favoriteList: List<FavoriteMovie>
    private var isFiltered: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initRecyclerview()
    }

    private fun initRoom() {
        context?.let {
            lifecycleScope.launch(IO) {
                roomDAO = FavoriteMovieRoom.favoriteMoviesRoom(it).favoriteMovieDAO()
                favoriteList = roomDAO.getFavoriteMovies()

                withContext(Main) {
                    if (favoriteList.isEmpty()) {
                        delay(1000)
                        binding.rvFavorites.visibility = GONE
                        binding.loadingFavorite.visibility = GONE
                        binding.favoritesEmptyListScreen.root.visibility = VISIBLE

                    } else {
                        adapter.updateFavoriteList(favoriteList)

                        delay(1000)
                        binding.favoritesEmptyListScreen.root.visibility = GONE
                        binding.loadingFavorite.visibility = GONE
                        binding.rvFavorites.visibility = VISIBLE
                    }
                }
            }
        }
    }

    private fun initUI() {
        binding.favoritesEmptyListScreen.root.visibility = GONE

        val main = (activity as MainActivity)
        main.displayToolbar(R.id.favoritesFragment)
        main.showToolbarDivider(false)
        main.showToolbar(true)
        main.showFAB(false)
    }

    private fun initRecyclerview() {
        adapter = FavoritesAdapter { onFavoriteMovieAction(it) }
        binding.rvFavorites.adapter = adapter

        binding.rvFavorites.layoutManager = GridLayoutManager(context, 2)

        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvFavorites)
    }

    private fun onFavoriteMovieAction(action: MovieAction) {
        when (action) {
            is ShowMovieDetails -> openMovieDetails(action.movie)
            is AddToFavorite -> addToFavorites(action)
            else -> {}
        }
    }

    private fun openMovieDetails(movie: MovieResult) {
        val intent = Intent(activity, MoviesDetailsActivity::class.java)
        intent.putExtra(DETAILS_KEY, movie.id)
        startActivity(intent)
    }

    private fun addToFavorites(action: AddToFavorite) {
        favoriteList.find { it.id == action.movie.id }?.isInFavorite = action.add
    }

    private suspend fun saveFavoriteList() {
        if(::favoriteList.isInitialized){
            val removeMovies = favoriteList.filter { !it.isInFavorite }.map { it.id }
            roomDAO.removeMoviesByIds(removeMovies)

            favoriteList = favoriteList.filterNot { !it.isInFavorite }.sortAsc()
            roomDAO.updateFavoriteMovies(favoriteList)
        }
    }

    fun filterList(text: String) {
        if (::adapter.isInitialized) {
            isFiltered = text.isNotEmpty()
            if (isFiltered) {
                val filteredList =
                    favoriteList.filter { it.title.uppercase().contains(text.uppercase()) }
                adapter.updateFavoriteList(filteredList)

            } else {
                adapter.updateFavoriteList(favoriteList.toList())
            }
        }
    }

    fun refreshFavorites() {
        lifecycleScope.launch(Main) {
            withContext(IO) { saveFavoriteList() }

            binding.rvFavorites.visibility = INVISIBLE
            binding.loadingFavorite.visibility = VISIBLE
            binding.favoritesEmptyListScreen.root.visibility = GONE

        }.invokeOnCompletion {
            initRoom()
        }
    }

    fun changeDisplay(smallDisplay: Boolean) {
        binding.rvFavorites.layoutManager = GridLayoutManager(context, if (smallDisplay) 3 else 2)
        adapter.updateFavoriteList(favoriteList.toList())
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch(IO) { saveFavoriteList() }
    }

    override fun onResume() {
        super.onResume()
        refreshFavorites()
    }

    private fun List<FavoriteMovie>.sortAsc(): List<FavoriteMovie> {
        return this.mapIndexed { index, movie ->
            FavoriteMovie(movie.id, movie.title, movie.posterPath, index + 1, movie.isInFavorite)
        }
    }

    private fun List<FavoriteMovie>.log():String{
        return "\n\t" + this.map {
            listOf(it.id, it.position, it.title, it.isInFavorite)
        }.joinToString("\n\t")
    }

    private val simpleCallback = object : SimpleCallback(UP or DOWN or START or END, 0) {
        private var start: Int? = null

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder,
            target: ViewHolder
        ): Boolean {
            recyclerView.adapter?.notifyItemMoved(
                viewHolder.adapterPosition,
                target.adapterPosition
            )
            return true
        }

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            (activity as MainActivity).enableSwipe(false)

            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                start = viewHolder?.adapterPosition

                (viewHolder as FavoritesViewHolder)
                    .binding
                    .favoriteSelectedBackground
                    .show(VISIBLE, context!!)
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            (activity as MainActivity).enableSwipe(true)

            (viewHolder as FavoritesViewHolder)
                .binding
                .favoriteSelectedBackground
                .show(GONE, context!!)

            start?.let {
                val end: Int = viewHolder.adapterPosition

                val isEndValid = end >= 0 && end < favoriteList.size
                val isStartValid = it >= 0 && it < favoriteList.size

                if (isStartValid && isEndValid) {
                    val temp = favoriteList.minus(favoriteList[it]).toMutableList()
                    temp.add(end, favoriteList[it])
                    favoriteList = temp.sortAsc()

                } else {
                    Log.e("ññ", "CLEARVIEW ERROR: Start and End values")
                    Log.e("ññ", "start=$start, end=$end")
                }
            } ?: kotlin.run {
                Log.e("ññ", "CLEARVIEW ERROR: Start empty value")
                Log.e("ññ", "start=$start")
            }
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {}

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
            return makeMovementFlags(if (isFiltered) 0 else UP or DOWN or START or END, 0)
        }
    }
}