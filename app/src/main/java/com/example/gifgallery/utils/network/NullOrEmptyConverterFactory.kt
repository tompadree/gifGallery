package com.example.gifgallery.utils.network


import androidx.annotation.Nullable
import java.lang.reflect.Type

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

class NullOnEmptyConverterFactory : Converter.Factory() {

    @Nullable
    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> { body ->
            if (body.contentLength() == 0L) EmptyBody() else delegate.convert(
                body
            )
        }
    }
}

class EmptyBody