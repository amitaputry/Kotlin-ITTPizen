package com.ta.ittpizen.feature_home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.ta.ittpizen.domain.model.PostItem
import com.ta.ittpizen.domain.model.PostItemType
import com.ta.ittpizen.domain.model.post.Post
import com.ta.ittpizen.domain.model.preference.UserPreference
import com.ta.ittpizen.domain.utils.DataPostItem
import com.ta.ittpizen.ui.component.post.PostItem
import com.ta.ittpizen.ui.component.tab.BaseScrollableTabRow
import com.ta.ittpizen.ui.component.topappbar.HomeTopAppBar
import com.ta.ittpizen.ui.theme.ITTPizenTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@ExperimentalFoundationApi
@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    navigateToMyProfileScreen: (String) -> Unit = {},
    navigateToUserProfileScreen: (String) -> Unit = {},
    navigateToNotificationScreen: (String) -> Unit = {},
    navigateToDetailPostScreen: (String) -> Unit = {},
    navigateToPhotoDetailScreen: (String) -> Unit = {},
) {

    val tabs = listOf("All Post", "Tweet", "Academic", "#PrestasiITTP", "Events", "Scholarship")

    val context = LocalContext.current
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    val userPreference by viewModel.userPreference.collectAsStateWithLifecycle(initialValue = UserPreference())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val allPostLoaded = uiState.allPostLoaded
    val allPost = uiState.allPost.collectAsLazyPagingItems()

    val selectedTabIndex by remember(key1 = pagerState.currentPage) {
        mutableIntStateOf(pagerState.currentPage)
    }

    val postItems = remember { mutableStateListOf<PostItem>() }

    val onTabSelected: (Int) -> Unit = {
        scope.launch {
            pagerState.scrollToPage(it)
        }
    }

    val getPostByType: (type: PostItemType) -> List<PostItem>  = { type ->
        postItems.filter { it.postType == type }
    }

    val onLikeClicked: (Post) -> Unit = { post ->
        if (post.liked) {
            viewModel.deletePostLike(token = userPreference.accessToken, postId = post.id)
        } else {
            viewModel.createPostLike(token = userPreference.accessToken, postId = post.id)
        }
        allPost.refresh()
    }

    val onShareClicked: (Post) -> Unit = { post ->
        val text = buildString {
            append(post.text)
            append("\n\n")
            append("By ITTPizen")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    LaunchedEffect(key1 = Unit) {
        postItems.addAll(DataPostItem.generateAllPost())
    }

    LaunchedEffect(key1 = userPreference) {
        if (userPreference.accessToken.isEmpty()) return@LaunchedEffect
        if (allPostLoaded) return@LaunchedEffect
        viewModel.getAllPost(token = userPreference.accessToken)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(
                onProfileClick = { navigateToMyProfileScreen(userPreference.userId) },
                onNotificationClick = { navigateToNotificationScreen(userPreference.userId) },
                profile = userPreference.photo
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            BaseScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                onSelected = onTabSelected,
                edgePadding = 20.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                LazyColumn {
                    items(count = allPost.itemCount, key = { it }) {
                        val post = allPost[it]
                        if (post != null) {
                            PostItem(
                                post = post,
                                onProfileClick = { navigateToUserProfileScreen(it.user.id) },
                                onClick = { navigateToDetailPostScreen(it.id) },
                                onPhotoClick = { navigateToPhotoDetailScreen(it) },
                                onLike = { onLikeClicked(post) },
                                onComment = { navigateToDetailPostScreen(post.id) },
                                onSend = onShareClicked,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Preview
@Composable
fun PreviewHomeScreen() {
    ITTPizenTheme {
        Surface {
            HomeScreen()
        }
    }
}
