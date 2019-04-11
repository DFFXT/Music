package com.web.moudle.search.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.BaseObserver
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.config.Shortcut
import com.web.moudle.search.bean.DefSearchRes
import com.web.moudle.search.bean.SearchResItem
import com.web.moudle.search.bean.SearchSug
import io.reactivex.disposables.Disposable

class SearchViewModel : ViewModel() {

    companion object {
        @JvmStatic
        val CODE_INTERNET_ERROR = 0
    }

    val searchSug = MutableLiveData<List<SearchResItem>>()
    val status = MutableLiveData<LiveDataWrapper<Any>>()
    private var wrapper = LiveDataWrapper<Any>()

    val defSearchRes = MutableLiveData<List<SearchResItem>>()

    private val model = SearchModel()

    private var disposable: Disposable? = null
    fun getSearchSug(word: String) {
        if(Shortcut.isStrictEmpty(word))return
        if(disposable!=null){
            disposable?.dispose()
        }
        model.getSearchSug(word)
                .subscribe(object : BaseObserver<List<SearchResItem>>() {
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(res: List<SearchResItem>) {
                        searchSug.value = res
                    }

                    override fun error(e: Throwable) {
                        e.printStackTrace()
                        wrapper.code = CODE_INTERNET_ERROR
                        status.value = wrapper
                    }
                })
    }

    fun defSearch(){
        model.defSearch(System.currentTimeMillis())
                .get(onNext = {
                    defSearchRes.value=it
                },onError = {
                    it.printStackTrace()
                })
    }

    fun refreshHistory(){
        model.refreshHistory()
                .get(onNext = {
                    defSearchRes.value=it
                })
    }
}