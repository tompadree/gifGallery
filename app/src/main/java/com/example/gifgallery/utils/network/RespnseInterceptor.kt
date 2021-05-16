package com.example.gifgallery.utils.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.UnknownHostException

/**
 * @author Tomislav Curis
 */

class ResponseInterceptor(
        private val internetConnectionManager: InternetConnectionManager
) : Interceptor {


    private val UNAUTHORIZED_EXCEPTION_CODE = 401


    override fun intercept(chain: Interceptor.Chain?): Response {
        try {
            if (!internetConnectionManager.hasInternetConnection()) {
                throw InternetConnectionException()
            }

            val request = chain!!.request()
            val response = chain.proceed(request)

            val responseCode = response.code()

            if (responseCode > 399) {
                if (responseCode == UNAUTHORIZED_EXCEPTION_CODE) {

                }

                throw NetworkException(response)
            }

            return response
        } catch(e: UnknownHostException){
            throw InternetConnectionException()
        } catch (e: NoInternetException) {
            throw e
        } catch (e: NetworkException) {
            throw e
        } catch (e: InternetConnectionException) {
            e.printStackTrace()
            throw InternetConnectionException()
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            throw NetworkException(null)
        }
    }
}