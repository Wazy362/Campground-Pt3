package com.codepath.lab6

import android.os.Bundle
import android.util.Log // <-- ADDED: Needed for Log.e/Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager // <-- ADDED: Needed for LayoutManager
import androidx.recyclerview.widget.RecyclerView // <-- ADDED: Needed for RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient // <-- ADDED: Needed for client
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler // <-- ADDED: Needed for handler
import com.codepath.lab6.BuildConfig
import kotlinx.serialization.json.Json // <-- ADDED: Needed for createJson()
import kotlinx.serialization.json.* // <-- CRITICAL FIX: Wildcard import for decodeFromString
import okhttp3.Headers
import org.json.JSONException // <-- ADDED: Needed for catch block


fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

private const val TAG = "ParksFragment"
private const val API_KEY = BuildConfig.API_KEY
private val PARKS_URL =
    "https://developer.nps.gov/api/v1/parks?api_key=${API_KEY}" // Changed to 'val'

class ParksFragment : Fragment() {
    // --- Fragment Properties ---
    private val parks = mutableListOf<Park>()
    private lateinit var parksRecyclerView: RecyclerView
    private lateinit var parksAdapter: ParksAdapter

    // --- Networking Function (Must be inside the class) ---
    private fun fetchParks() {
        val client = AsyncHttpClient()
        client.get(PARKS_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.i(TAG, "Successfully fetched parks: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        parks.addAll(list)
                        parksAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) { // Changed to Exception to catch KotlinX serialization errors
                    Log.e(TAG, "Exception during JSON parsing: $e")
                }
            }
        })
    }

    // --- Lifecycle Methods ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Change this statement to store the view in a variable instead of a return statement
        val view = inflater.inflate(R.layout.fragment_parks, container, false)

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        parksRecyclerView = view.findViewById(R.id.parks)
        parksRecyclerView.layoutManager = layoutManager
        parksRecyclerView.setHasFixedSize(true)
        parksAdapter = ParksAdapter(view.context, parks)
        parksRecyclerView.adapter = parksAdapter

        // Update the return statement to return the inflated view from above
        return view
    }

    // ADDED: This method is required to call fetchParks() after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchParks()
    }

    companion object {
        fun newInstance(): ParksFragment {
            return ParksFragment()
        }
    }
}