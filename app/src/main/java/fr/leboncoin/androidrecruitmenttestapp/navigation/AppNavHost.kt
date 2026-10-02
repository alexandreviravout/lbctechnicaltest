package fr.leboncoin.androidrecruitmenttestapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.leboncoin.androidrecruitmenttestapp.ui.albumdetails.AlbumDetailsScreen
import fr.leboncoin.androidrecruitmenttestapp.ui.albums.AlbumsScreen
import fr.leboncoin.androidrecruitmenttestapp.ui.navigation.AlbumDetails
import fr.leboncoin.androidrecruitmenttestapp.ui.navigation.AlbumList

/**
 * This composable defines the navigation host.
 * It allows the navigation between the album list and the album details.
 *
 * @author alexandre.viravout
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AlbumList,
        modifier = modifier,
    ) {
        composable<AlbumList> {
            AlbumsScreen(
                onItemSelected = { id ->
                    navController.navigate(route = AlbumDetails(albumId = id))
                },
            )
        }

        composable<AlbumDetails> {
            AlbumDetailsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}