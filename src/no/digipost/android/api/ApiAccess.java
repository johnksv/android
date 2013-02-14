/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.android.api;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import no.digipost.android.model.Account;
import no.digipost.android.model.Documents;
import no.digipost.android.model.Letter;
import android.os.AsyncTask;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

public class ApiAccess {

	public ApiAccess() {
	}

	public Account getPrimaryAccount(final String access_token) {
		return (Account) JSONConverter.processJackson(Account.class, getApiJsonString(access_token, ApiConstants.URL_API));
	}

	public Documents getDocuments(final String access_token, final String uri) {
		return (Documents) JSONConverter.processJackson(Documents.class, getApiJsonString(access_token, uri));
	}

	public String getApiJsonString(final String access_token, final String uri) {
		Client client = Client.create();

		Builder builder = client
				.resource(uri)
				.header(ApiConstants.ACCEPT, ApiConstants.APPLICATION_VND_DIGIPOST_V2_JSON)
				.header(ApiConstants.AUTHORIZATION, ApiConstants.BEARER + access_token);

		ApiGetJsonStringTask apiJsonStringTask = new ApiGetJsonStringTask();
		String jsonString = null;

		try {
			jsonString = apiJsonStringTask.execute(builder).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonString;
	}

	public Letter getMovedDocument(final String access_token, final String uri, final String se) {
		return (Letter) JSONConverter.processJackson(Letter.class, moveLetter(access_token, uri, se));
	}

	public String moveLetter(final String access_token, final String uri, final String json) {
		Client client = Client.create();

		Builder builder = client
				.resource(uri)
				.header(ApiConstants.CONTENT_TYPE, ApiConstants.APPLICATION_VND_DIGIPOST_V2_JSON)
				.header(ApiConstants.ACCEPT, ApiConstants.APPLICATION_VND_DIGIPOST_V2_JSON)
				.header(ApiConstants.AUTHORIZATION, ApiConstants.BEARER + access_token)
				.entity(json);

		System.out.println("JSON: " + json);
		System.out.println("URI: " + uri);

		ApiPostTask postapi = new ApiPostTask();
		String jsonString = null;

		try {
			jsonString = postapi.execute(builder).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonString;
	}

	public InputStream getDocumentContent(final String access_token, final String uri) {
		Client client = Client.create();

		Builder builder = client
				.resource(uri)
				.header(ApiConstants.ACCEPT, ApiConstants.CONTENT_OCTET_STREAM)
				.header(ApiConstants.AUTHORIZATION, ApiConstants.BEARER + access_token);

		ApiGetDocumentContentTask apiGetContent = new ApiGetDocumentContentTask();
		InputStream is = null;
		try {
			is = apiGetContent.execute(builder).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	private class ApiGetJsonStringTask extends AsyncTask<Builder, Void, String> {
		@Override
		protected String doInBackground(final Builder... params) {
			InputStream is = params[0].get(ClientResponse.class).getEntityInputStream();
			return JSONConverter.getJsonStringFromInputStream(is);
		}
	}

	private class ApiGetDocumentContentTask extends AsyncTask<Builder, Void, InputStream> {

		@Override
		protected InputStream doInBackground(final Builder... params) {
			ClientResponse cr = params[0].get(ClientResponse.class);
			if (cr.getStatus() != 200) {
				System.out.println("Feil: " + cr.getStatus());
				System.out.println("Json error: " + JSONConverter.getJsonStringFromInputStream(cr.getEntityInputStream()));
			}
			return cr.getEntityInputStream();
		}
	}

	private class ApiPostTask extends AsyncTask<Builder, Void, String> {
		@Override
		protected String doInBackground(final Builder... params) {
			// TODO Auto-generated method stub
			InputStream is = params[0].post(ClientResponse.class).getEntityInputStream();
			return JSONConverter.getJsonStringFromInputStream(is);
		}
	}
}