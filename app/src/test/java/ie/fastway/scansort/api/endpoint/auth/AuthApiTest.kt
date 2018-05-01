package ie.fastway.scansort.api.endpoint.auth

import ie.fastway.scansort.api.ApiEndpointTester
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsInstanceOf
import org.junit.Before
import org.junit.Test

/**
 *
 */
class AuthApiTest {

    lateinit var apiTester: ApiEndpointTester
    lateinit var shipmentApi: AuthApi

    @Before
    @Throws(Exception::class)
    fun setUp() {
        apiTester = ApiEndpointTester()
    }

    @Test
    fun canConvertToJsonOk() {

        val convertedJson = apiTester.gson.fromJson(
                Json.RESPONSE_OK, AuthResponseJson::class.java)

        MatcherAssert.assertThat(convertedJson.data,
                IsInstanceOf.instanceOf(AuthTokenJson::class.java))
    }

    object Json {
        const val RESPONSE_OK = """
                {
                  "data": {
                    "token_type": "Bearer",
                    "expires_in": 31536000,
                    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjI2MzYzMWNlM2NkNzlmYWZhNjVkMTZiOWJiNTgzNjA3YWM2ZWEzNWZlMWQ0MGZjYzgyMjk4NjNhNDBhMzJiZjg2MzQ1YWIwYmNjMmJlNTUxIn0.eyJhdWQiOiIxIiwianRpIjoiMjYzNjMxY2UzY2Q3OWZhZmE2NWQxNmI5YmI1ODM2MDdhYzZlYTM1ZmUxZDQwZmNjODIyOTg2M2E0MGEzMmJmODYzNDVhYjBiY2MyYmU1NTEiLCJpYXQiOjE1MTAwNzM1NTEsIm5iZiI6MTUxMDA3MzU1MSwiZXhwIjoxNTQxNjA5NTUxLCJzdWIiOiIiLCJzY29wZXMiOltdfQ.zXt2SZNwDd6k2xw0siUWN6gNcVMXeYB8VNVyLAknbUpTTZHCKk8N0MrCHEbS_E3uWYpmkCQjyUx0cJVEMQgkpu5f-QP93jOd2yxIhB8mcq4ERTRyWDsoLPHNLxxpTqtjFTUjSrYmhB_t7FyQ991Paby6Ebt8EkqUQxrjWMHUICS5cXD1QWgNF7ZVhoHDi0xzKsaBDFrtDDKSo4gbZOyPH6vpaWbXO4__2rwUCrWbMzRjIWqTJ8QjkNSH-28Gce8EzSGBFVPklAewPvmTUR18MX6GmEre2CYqh6vxS53oN4MjYONuFqF2fIqhLi5YOMV8MgdJWnxBFX3fXtbtur0a2CgzFstQVlB6z_6kqlIxmtK-18AN5k3uhXjgH_AnwhrDIsReaLAASv5_VIVoLUT5MzodOGbJrnt25fhF5gTb2lhMu-6LtoaNZC3XIgEfNytfdckW5WgEzdrKt2HpcANw9HogP1XJtY8GEFZ22oL2CGhi68BmXBD7xsytmxLXeZjHJr-G8CHI609v01xE9E67dIp4jyERCx3ykS-C2WEttraRM1SVJziZDWQFQFgeDRiYpgxX9gvlO3bvkITAUFzyT-NiDmKozsTL-sBwgGIENG11G8tMmM-H8xQbfHzi8FRHCbhM0U_s2rFuCDB7vMnrgwmVUcZTOwH4zDj1--TRjMM"
                  }
                }
            """
    }

}