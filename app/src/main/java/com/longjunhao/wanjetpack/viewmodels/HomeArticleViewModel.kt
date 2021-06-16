package com.longjunhao.wanjetpack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.longjunhao.wanjetpack.data.ApiArticle
import com.longjunhao.wanjetpack.data.WanJetpackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * .HomeArticleViewMode
 *
 * @author Admitor
 * @date 2021/05/24
 */
@HiltViewModel
class HomeArticleViewModel @Inject constructor(
    private val repository: WanJetpackRepository
) : ViewModel() {
    private var currentArticleResult: Flow<PagingData<ApiArticle>>? = null

    fun getHomeArticle(): Flow<PagingData<ApiArticle>> {
        val newResult: Flow<PagingData<ApiArticle>> =
            repository.getHomeArticle().cachedIn(viewModelScope)
        currentArticleResult = newResult
        return newResult
    }

    /**
     * todo ：和adapter一样，重复的部分应该可以写在baseViewModel中
     */
    fun collect(id: Int) = repository.collect(id)

    /**
     * todo ：和adapter一样，重复的部分应该可以写在baseViewModel中
     */
    fun unCollect(id: Int) = repository.unCollect(id)
}