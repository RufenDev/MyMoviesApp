package com.example.mymoviesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.example.mymoviesapp.R
import com.example.mymoviesapp.adapter.details.DetailsAdapter
import com.example.mymoviesapp.api.APIClient
import com.example.mymoviesapp.api.APIClient.IMAGE_PATH_ORIGINAL
import com.example.mymoviesapp.api.DetailsItem
import com.example.mymoviesapp.api.MovieActor
import com.example.mymoviesapp.api.MovieCrew
import com.example.mymoviesapp.api.MovieDetails
import com.example.mymoviesapp.api.MovieResult
import com.example.mymoviesapp.databinding.ActivityMoviesDetailsBinding
import com.example.mymoviesapp.databinding.LayoutMovieDetailsBinding
import com.example.mymoviesapp.interfaces.MovieConstants.DETAILS_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_ID_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_SUBTITLE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TITLE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MORE_TYPE_KEY
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_MOVIE_CAST
import com.example.mymoviesapp.interfaces.MovieConstants.SEE_RECOMMENDED_MOVIES
import com.example.mymoviesapp.interfaces.MovieConstants.copyToClipboard
import com.example.mymoviesapp.interfaces.MovieConstants.downloadImage
import com.example.mymoviesapp.interfaces.MovieConstants.openDialog
import com.example.mymoviesapp.interfaces.MovieConstants.show
import com.example.mymoviesapp.room.FavoriteMovie
import com.example.mymoviesapp.room.FavoriteMovieDAO
import com.example.mymoviesapp.room.FavoriteMovieDatabase
import com.example.mymoviesapp.room.FavoriteMovieRoom
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoviesDetailsBinding
    private lateinit var layout: LayoutMovieDetailsBinding

    private lateinit var castAdapter: DetailsAdapter
    private lateinit var recommendedAdapter: DetailsAdapter

    private var movieID: Int? = null
    private lateinit var movieDetails: MovieDetails
    private lateinit var movieEnglishDetails: MovieDetails
    private lateinit var movieCast: List<MovieActor>
    private lateinit var movieCrew: List<MovieCrew>
    private lateinit var movieRecommended: List<MovieResult>

    private lateinit var room: FavoriteMovieDatabase
    private lateinit var roomDAO: FavoriteMovieDAO

    private var isDialogOpen: Boolean = false
    private var isMovieInFavoriteList: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layout = binding.detailsLayout

        movieID = intent?.extras?.getInt(DETAILS_KEY)

        initUI()
        initComponents()
        initListeners()
        initAPI()
        initRoom()
    }

    private fun initRoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            room = FavoriteMovieRoom.favoriteMoviesRoom(this@MoviesDetailsActivity)
            roomDAO = room.favoriteMovieDAO()

            movieID?.let {
                isMovieInFavoriteList = roomDAO.isMovieInFavorites(it)

                withContext(Dispatchers.Main) {
                    layout.checkDetailsFavorite.isChecked = isMovieInFavoriteList
                }
            }
        }
    }

    private fun initUI() {
        setSupportActionBar(binding.detailsToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        binding.swipeDetailsError.visibility = GONE
        binding.detailsAppBar.visibility = GONE
        binding.detailsNestedScroll.visibility = GONE

        binding.pbDetailsLoading.visibility = VISIBLE
        window.statusBarColor = getColor(R.color.details_semitransparent)
    }

    private fun initComponents() {
        layout.rvMovieCast.layoutManager = GridLayoutManager(this, 4)
        castAdapter = DetailsAdapter { onItemClicked(it) }
        layout.rvMovieCast.adapter = castAdapter

        layout.rvMoviesRecommended.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        recommendedAdapter = DetailsAdapter { onItemClicked(it) }
        layout.rvMoviesRecommended.adapter = recommendedAdapter
    }

    private fun initListeners() {
        binding.btnDetailsBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        layout.cardSynopsisReadMore.setOnClickListener {
            isReadMoreChecked = !isReadMoreChecked
        }

        layout.btnCastSeeMore.setOnClickListener {
            navigateToSeeMore(true)
        }

        layout.btnRecommendedSeeMore.setOnClickListener {
            navigateToSeeMore(false)
        }

        binding.swipeDetailsError.setOnRefreshListener {
            initAPI()
        }

        binding.btnDetailsOptions.setOnClickListener {
            openMovieOptions()
        }

        layout.checkDetailsFavorite.setOnClickListener {
            addToFavorite(!isMovieInFavoriteList)
        }
    }

    private fun addToFavorite(checked: Boolean): Boolean {
        lifecycleScope.launch(Dispatchers.IO) {

            if (::movieEnglishDetails.isInitialized) {
                val msj: String =
                    if (checked) {
                        val lastPosition = roomDAO.getLastPosition() + 1

                        val newMovie = FavoriteMovie(
                            movieEnglishDetails.id,
                            movieEnglishDetails.title,
                            movieEnglishDetails.posterPath,
                            lastPosition,
                            true
                        )

                        roomDAO.addMovieToFavorites(newMovie)
                        getString(R.string.added_to_favorites)

                    } else {
                        val targetMovie =
                            roomDAO.getFavoriteMovieFromId(movieDetails.id)

                        roomDAO.removeMovieById(movieDetails.id)
                        roomDAO.subtractPositionAfterOldPosition(targetMovie.position)

                        getString(R.string.removed_from_favorites)
                    }

                withContext(Dispatchers.Main) {
                    isMovieInFavoriteList = checked
                    layout.checkDetailsFavorite.isChecked = isMovieInFavoriteList
                    Toast.makeText(this@MoviesDetailsActivity, msj, Toast.LENGTH_SHORT).show()
                }
            }

        }
        return true
    }

    private fun openMovieOptions() {
        val popup = PopupMenu(this, binding.btnDetailsOptions)
        popup.inflate(R.menu.details_menu)

        popup.menu.findItem(R.id.menuDetailsFavorite).title =
            getString(
                if (isMovieInFavoriteList) {
                    R.string.remove_from_favorites
                } else {
                    R.string.add_to_favorites
                }
            )

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuDetailsCopy -> copyToClipboard(this, movieEnglishDetails.title)
                R.id.menuDetailsFavorite -> {
                    addToFavorite(!isMovieInFavoriteList)
                }

                R.id.menuDetailsDownload -> downloadImage(
                    this,
                    movieEnglishDetails.posterPath,
                    movieEnglishDetails.title
                )

                R.id.menuDetailsPoster -> {
                    if (!isDialogOpen) {
                        isDialogOpen = true

                        openDialog(this, movieEnglishDetails.posterPath)
                            .setOnDismissListener { isDialogOpen = false }
                    }
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun navigateToSeeMore(seeMoreCast: Boolean) {
        val intent = Intent(this, MovieSeeMoreActivity::class.java)

        intent.putExtra(SEE_MORE_ID_KEY, movieID)
        intent.putExtra(SEE_MORE_SUBTITLE_KEY, movieEnglishDetails.title)

        if (seeMoreCast) {
            intent.putExtra(SEE_MORE_TYPE_KEY, SEE_MOVIE_CAST)
            intent.putExtra(SEE_MORE_TITLE_KEY, getString(R.string.movie_cast))

        } else {
            intent.putExtra(SEE_MORE_TYPE_KEY, SEE_RECOMMENDED_MOVIES)
            intent.putExtra(SEE_MORE_TITLE_KEY, getString(R.string.details_recommended))
        }
        startActivity(intent)
    }

    private fun initAPI() {
        lifecycleScope.launch {

            movieID?.let { id ->
                try {
                    movieDetails = APIClient.service.getMovieDetails(id)
                    movieEnglishDetails = APIClient.service.getMovieDetails(id, lan = "en-US")
                    movieRecommended = APIClient.service.getRecommendedMovies(id).results
                    val movieCredits = APIClient.service.getMovieCredits(id)
                    movieCast = movieCredits.cast
                    movieCrew = movieCredits.crew

                    showDetails()
                    binding.swipeDetailsError.show(GONE, this@MoviesDetailsActivity)

                    binding.detailsAppBar.show(VISIBLE, this@MoviesDetailsActivity)
                    binding.detailsNestedScroll.show(VISIBLE, this@MoviesDetailsActivity)
                    window.statusBarColor = getColor(android.R.color.transparent)

                } catch (e: Exception) {
                    delay(1000)
                    binding.swipeDetailsError.show(VISIBLE, this@MoviesDetailsActivity)

                    binding.detailsAppBar.show(GONE, this@MoviesDetailsActivity)
                    binding.detailsNestedScroll.show(GONE, this@MoviesDetailsActivity)

                } finally {
                    binding.pbDetailsLoading.show(GONE, this@MoviesDetailsActivity)
                    binding.swipeDetailsError.isRefreshing = false
                }

            } ?: kotlin.run {
                delay(1000)
                binding.pbDetailsLoading.show(GONE, this@MoviesDetailsActivity)

                binding.detailsAppBar.show(GONE, this@MoviesDetailsActivity)
                binding.detailsNestedScroll.show(GONE, this@MoviesDetailsActivity)

                binding.swipeDetailsError.show(VISIBLE, this@MoviesDetailsActivity)
                binding.swipeLayout.tvSwipeErrorTitle.text =
                    getString(R.string.movie_not_found_title)
                binding.swipeLayout.tvSwipeErrorSubtitle.text =
                    getString(R.string.movie_not_found_subtitle)
            }
        }
    }

    private fun showDetails() {
        layout.tvMovieDetailsTitle.text = movieEnglishDetails.title

        layout.tvMovieDetails.text = listOf(
            movieDetails.releaseDate.take(4),
            movieDetails.genres.joinToString(", ") { it.name },
            movieDetails.duration.toHour()
        ).joinToString(" ● ")

        layout.tvMovieRating.text = String.format("%.1f", movieDetails.voteAverage)
        layout.movieRatingBar.rating = movieDetails.voteAverage.toFloat() / 2

        if (movieDetails.synopsis.isNotEmpty()) {
            layout.tvSynopsis.text = movieDetails.synopsis

        } else {
            layout.cardSynopsisReadMore.visibility = GONE
            layout.tvSynopsis.visibility = GONE
            layout.tvSynopsisTitle.visibility = GONE
            layout.dividerRating.visibility = GONE
        }

        layout.tvSynopsis.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layout.tvSynopsis.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val lineCount = layout.tvSynopsis.lineCount

                if (lineCount <= 3) layout.cardSynopsisReadMore.visibility = GONE
                else layout.tvSynopsis.maxLines = 3
            }
        })

        setCast()
        setProduction()
        setRecommended()

        Picasso.get()
            .load(IMAGE_PATH_ORIGINAL + movieEnglishDetails.posterPath)
            .into(binding.ivDetailsMovieCover, object : Callback {
                override fun onSuccess() {
                    binding.detailsAppbarLoading.visibility = GONE
                    binding.ivDetailsMovieCover.visibility = VISIBLE
                    binding.detailsAppbarShadow.visibility = VISIBLE
                }

                override fun onError(e: java.lang.Exception?) {
                    binding.ivDetailsMovieCover.setImageResource(R.drawable.image_not_found)
                    binding.detailsAppbarLoading.visibility = GONE
                    binding.ivDetailsMovieCover.visibility = VISIBLE
                    binding.detailsAppbarShadow.visibility = VISIBLE
                }
            })
    }

    private fun setCast() {
        val cast: List<DetailsItem> = movieCast.map {
            DetailsItem(true, it.id, it.photoPath, it.name, it.character)
        }.take(4)
        castAdapter.updateItemList(cast)
        if (movieCast.size < 5) {
            layout.cardCastSeeMore.visibility = GONE

            if (movieCast.isEmpty()) {
                layout.rvMovieCast.visibility = GONE
                layout.dividerSynopsis.visibility = GONE
                layout.tvMovieCastTitle.visibility = GONE
            }
        }
    }

    private fun setProduction() {
        val directors: String = movieCrew
            .filter { it.job.lowercase() == "director" }
            .joinToString("\n") { it.name }

        if (directors.isNotEmpty()) {
            layout.tvMovieDirectorsName.text = directors

        } else {
            layout.tvMovieDirectorsName.visibility = GONE
            layout.tvMovieDirectorsTitle.visibility = GONE
        }

        val producers: String = movieCrew
            .filter { it.job.lowercase() == "producer" }
            .joinToString("\n") { it.name }

        if (producers.isNotEmpty()) {
            layout.tvMovieProductorsName.text = producers

        } else {
            layout.tvMovieProductorsName.visibility = GONE
            layout.tvMovieProductorsTitle.visibility = GONE
        }

        val screenplays: String = movieCrew
            .filter { it.job.lowercase() == "screenplay" }
            .joinToString("\n") { it.name }

        if (screenplays.isNotEmpty()) {
            layout.tvMovieScreenplayName.text = screenplays

        } else {
            layout.tvMovieScreenplayName.visibility = GONE
            layout.tvMovieScreenplayTitle.visibility = GONE
        }

        val companies: String = movieDetails.productionCompanies.joinToString("\n") { it.name }

        if (companies.isNotEmpty()) {
            layout.tvMovieCompaniesName.text = companies

        } else {
            layout.tvMovieCompaniesName.visibility = GONE
            layout.tvMovieCompaniesTitle.visibility = GONE
        }

        val homepage: String = movieDetails.homepage
        val englishHomepage: String = movieEnglishDetails.homepage

        if (homepage.isNotEmpty()) {
            layout.tvMovieHomepageLink.text = homepage

        } else if (englishHomepage.isNotEmpty()) {
            layout.tvMovieHomepageLink.text = englishHomepage

        } else {
            layout.tvMovieHomepageLink.visibility = GONE
            layout.tvMovieHomepageTitle.visibility = GONE
        }

        if (directors.isEmpty() &&
            producers.isEmpty() &&
            screenplays.isEmpty() &&
            companies.isEmpty() &&
            homepage.isEmpty() &&
            englishHomepage.isEmpty()
        ) {
            layout.dividerCast.visibility = GONE
            layout.tvMovieProductionTitle.visibility = GONE
            layout.layoutProduction.visibility = GONE
        }
    }

    private fun setRecommended() {
        val recommended: List<DetailsItem> = movieRecommended.map {
            DetailsItem(false, it.id, it.posterPath, it.title)
        }.shuffled().take(5)
        recommendedAdapter.updateItemList(recommended)
        if (movieRecommended.size < 5) {
            layout.btnRecommendedSeeMore.visibility = GONE

            if (movieRecommended.isEmpty()) {
                layout.dividerProduction.visibility = GONE
                layout.rvMoviesRecommended.visibility = GONE
                layout.tvMoviesRecommendedTitle.visibility = GONE
            }
        }
    }

    private fun onItemClicked(item: DetailsItem) {
        if (item.isAnActor) {
            if (!isDialogOpen) {
                isDialogOpen = true

                openDialog(
                    this,
                    item.imagePath.orEmpty(),
                    item.title,
                    item.subtitle
                ).setOnDismissListener { isDialogOpen = false }
            }

        } else {
            val intent = Intent(this, MoviesDetailsActivity::class.java)
            intent.putExtra(DETAILS_KEY, item.id)
            startActivity(intent)
        }
    }

    private var isReadMoreChecked: Boolean = false
        set(checked) {
            layout.tvSynopsisReadMore.text = getString(
                if (checked) R.string.movie_synopsis_read_less
                else R.string.movie_synopsis_read_more
            )
            layout.ivSynopsisReadMore.setImageResource(
                if (checked) R.drawable.ic_arrow_up
                else R.drawable.ic_arrow_down
            )
            layout.tvSynopsis.maxLines = if (checked) 20 else 3
            field = checked
        }


    private fun Int.toHour(): String {
        val hours: Int = this / 60
        val minusRes: Int = this % 60
        return "${hours}h ${minusRes}m"
    }
}
