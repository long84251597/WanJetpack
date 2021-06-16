package com.longjunhao.wanjetpack.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.longjunhao.wanjetpack.R
import com.longjunhao.wanjetpack.adapter.HomeArticleAdapter.ArticleViewHolder
import com.longjunhao.wanjetpack.data.ApiArticle
import com.longjunhao.wanjetpack.databinding.ListItemArticleBinding
import com.longjunhao.wanjetpack.viewmodels.HomeArticleViewModel

/**
 * .ArticleAdapter
 *
 * @author Admitor
 * @date 2021/05/21
 */
class HomeArticleAdapter(
    private val viewModel: HomeArticleViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : PagingDataAdapter<ApiArticle, ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ListItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null) {
            holder.bind(article)
            holder.binding.favorite.setOnClickListener { view ->
                //todo 如果不执行observe，不会执行网络请求，必须加上observe操作，why？
                //todo HomeArticleAdapter、ProjectAdapter、WechatAdapter、WendaAdapter共用的部分，是不是可以提取到BaseAdapter中呢？
                if (article.collect) {
                    viewModel.unCollect(article.id).observe(viewLifecycleOwner, Observer {
                        if (it.errorCode == 0) {
                            article.collect = false
                            holder.binding.favorite.setImageResource(R.drawable.ic_favorite_border_24)
                            Snackbar.make(view, "取消收藏成功", Snackbar.LENGTH_LONG).show()
                        } else if (it.errorCode == -1001) {
                            //todo 这是通过网络返回发现没有登录，是否需要多加个条件：新增Boolean类型的isLogin保存在SharedPreferences中呢？
                            Snackbar.make(view, "请先登录账号，待实现", Snackbar.LENGTH_LONG).show()
                        }
                    })
                } else {
                    viewModel.collect(article.id).observe(viewLifecycleOwner, Observer {
                        if (it.errorCode == 0) {
                            article.collect = true
                            holder.binding.favorite.setImageResource(R.drawable.ic_favorite_24)
                            Snackbar.make(view, "收藏成功", Snackbar.LENGTH_LONG).show()
                        } else if (it.errorCode == -1001) {
                            Snackbar.make(view, "请先登录账号，待实现", Snackbar.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }
    }

    class ArticleViewHolder(
        val binding: ListItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.article?.let { homeArticle ->
                    val uri = Uri.parse(homeArticle.link)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    it.context.startActivity(intent)
                }
            }
        }

        fun bind(item: ApiArticle) {
            binding.apply {
                article = item
                executePendingBindings()
            }
        }

    }

    private class ArticleDiffCallback : DiffUtil.ItemCallback<ApiArticle>() {
        override fun areItemsTheSame(oldItem: ApiArticle, newItem: ApiArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ApiArticle, newItem: ApiArticle): Boolean {
            return oldItem == newItem
        }

    }
}