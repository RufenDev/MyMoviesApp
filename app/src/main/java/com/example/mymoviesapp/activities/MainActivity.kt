package com.example.mymoviesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mymoviesapp.R
import com.example.mymoviesapp.api.MovieGenre
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ActivityMainBinding
import com.example.mymoviesapp.fragments.DiscoverFragment
import com.example.mymoviesapp.fragments.FavoritesFragment
import com.example.mymoviesapp.fragments.HomeFragment
import com.example.mymoviesapp.fragments.SearchFragment
import com.example.mymoviesapp.interfaces.MovieConstants.DETAILS_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_ID_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TITLE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TYPE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MOVIES_BY_GENRE
import com.example.mymoviesapp.interfaces.MovieConstants.show

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = getString(R.string.home_title)

        initComponents()
        initListeners()
    }

    private fun initComponents() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bnvMenu.setupWithNavController(navController)

        binding.btnHomeSearch.visibility = VISIBLE
    }

    private fun initListeners() {
        binding.fabMain.setOnClickListener {
            when (val fragment = getCurrentFragment()) {
                is HomeFragment -> {
                    fragment.scrollUp()
                    binding.appbarLayout.setExpanded(true, true)
                }

                is SearchFragment -> {
                    fragment.scrollUp()
                    binding.appbarLayout.setExpanded(true, true)
                }
            }
        }

        binding.swipeMain.setOnRefreshListener {
            when (val fragment = getCurrentFragment()) {

                is HomeFragment -> fragment.initAPI()

                is DiscoverFragment -> fragment.initAPI()

                is SearchFragment -> {
                    hideKeyboard(binding.svSearchMovie)
                    fragment.refreshSearch()
                }

                is FavoritesFragment -> {
                    hideKeyboard(binding.svSearchMovie)
                    fragment.refreshFavorites()
                    refreshingSwipe(false)
                }

                else -> binding.swipeMain.isRefreshing = false
            }
        }

        binding.btnHomeSearch.setOnClickListener {
            if (getCurrentFragment() is FavoritesFragment) {
                binding.layoutToolbarButtons.visibility = GONE
                binding.searchBarLayout.visibility = VISIBLE

            } else {
                binding.bnvMenu.selectedItemId = R.id.searchFragment
            }
        }

        binding.btnChangeDisplay.setOnClickListener {
            val fragment = getCurrentFragment()
            if (fragment is FavoritesFragment) {
                isDisplayChecked = !isDisplayChecked
                fragment.changeDisplay(isDisplayChecked)
            }
        }

        binding.btnSearchBack.setOnClickListener {
            if (getCurrentFragment() is FavoritesFragment) {

                hideKeyboard(binding.svSearchMovie)
                binding.searchBarLayout.visibility = GONE
                binding.layoutToolbarButtons.visibility = VISIBLE

                binding.svSearchMovie.setQuery("", true)

            } else {
                binding.bnvMenu.selectedItemId = R.id.homeFragment
            }
        }

        binding.svSearchMovie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val fragment = getCurrentFragment()
                hideKeyboard(binding.svSearchMovie)
                return if (fragment is SearchFragment) {
                    fragment.searchMovie(query?.trim() ?: "")
                    true
                } else {
                    false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val fragment = getCurrentFragment()
                return if (fragment is FavoritesFragment){
                    fragment.filterList(newText?.trim() ?: "")
                    true
                } else {
                    false
                }
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (
                    getCurrentFragment() is FavoritesFragment &&
                    binding.searchBarLayout.visibility == VISIBLE
                ) {
                    hideKeyboard(binding.svSearchMovie)
                    binding.searchBarLayout.visibility = GONE
                    binding.layoutToolbarButtons.visibility = VISIBLE

                    binding.svSearchMovie.setQuery("", true)

                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

    }

    private fun getCurrentFragment() = supportFragmentManager
        .findFragmentById(binding.navHostFragment.id)
        ?.childFragmentManager
        ?.fragments
        ?.first()

    fun openMovieDetails(movie: MovieResult) {
        val intent = Intent(this, MoviesDetailsActivity::class.java)
        intent.putExtra(DETAILS_KEY, movie.id)
        startActivity(intent)
    }

    fun seeMoreGenres(genre: MovieGenre) {
        val intent = Intent(this, MovieSeeMoreActivity::class.java)

        intent.putExtra(SEE_MORE_ID_KEY, genre.id)
        intent.putExtra(SEE_MORE_TITLE_KEY, genre.name)
        intent.putExtra(SEE_MORE_TYPE_KEY, SEE_MOVIES_BY_GENRE)

        startActivity(intent)
    }

    fun showFAB(visibility: Boolean) {
        if (visibility) {
            binding.fabMain.show()
        } else {
            binding.fabMain.hide()
        }
    }

    fun refreshingSwipe(refreshing: Boolean) {
        binding.swipeMain.isRefreshing = refreshing
    }

    fun showToolbar(visibility: Boolean) {
        binding.appbarLayout.setExpanded(visibility, false)
    }

    fun showToolbarDivider(visibility: Boolean) {
        binding.toolbarDivider.show(if (visibility) VISIBLE else GONE, this)
    }

    fun displayToolbar(destination: Int) {
        binding.layoutToolbarButtons.visibility = GONE
        binding.btnChangeDisplay.visibility = GONE
        binding.searchBarLayout.visibility = GONE

        when (destination) {
            R.id.homeFragment -> {
                binding.layoutToolbarButtons.visibility = VISIBLE
                supportActionBar?.title = getString(R.string.home_title)
            }

            R.id.searchFragment -> {
                binding.searchBarLayout.visibility = VISIBLE
                binding.svSearchMovie.setQuery("", false)
                supportActionBar?.title = ""
            }

            R.id.discoverFragment -> {
                binding.layoutToolbarButtons.visibility = VISIBLE
                supportActionBar?.title = getString(R.string.discover_title)
            }

            R.id.favoritesFragment -> {
                isDisplayChecked = false
                binding.layoutToolbarButtons.visibility = VISIBLE
                binding.btnChangeDisplay.visibility = VISIBLE
                binding.svSearchMovie.setQuery("", false)
                supportActionBar?.title = getString(R.string.favorites_title)
            }
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun enableSwipe(enable: Boolean) {
        binding.swipeMain.isEnabled = enable
    }

    private var isDisplayChecked: Boolean = false
        set(value) {
            binding.btnChangeDisplay.setImageResource(
                if (value) {
                    R.drawable.ic_display_normal
                } else {
                    R.drawable.ic_display_small
                }
            )
            field = value
        }
}