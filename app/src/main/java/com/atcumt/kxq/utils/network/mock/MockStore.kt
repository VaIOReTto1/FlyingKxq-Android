// src/main/java/com/atcumt/kxq/utils/network/mock/MockStore.kt
package com.atcumt.kxq.utils.network.mock

import com.atcumt.kxq.utils.network.ApiServiceS

object MockStore {
    /**
     * key: baseUrl + endpoint
     * value: Map<Regex, mockJson>
     */
    val maps: Map<String, Map<Regex, String>> = mapOf(

        // 登录
        ApiServiceS.BASE_URL_AUTH + "v1/login/username" to mapOf(
            // 匹配所有请求，统一返回本地 mock 数据
            Regex(".*") to """
            {
                "code": 200,
                "msg": "成功",
                "data": {
                    "accessToken": "IszKGVnxUpqvQiovNWt2llk1FzQmeJuSueaquzmP9axyUMMQkFZetTRfvpauJJ5r",
                    "expiresIn": 2592000,
                    "refreshToken": "paNGHEdxo3BZqR6V3is9r82PyOhFqtjQLWKNrkTqFA2JISSe3KDqU9Ac44qI7NJfoyBgoWPP5r2JCBY6uv5HVKzq3XLKsGpoUNxYPJQcOlFaDT9gR8b6mG5RlUZBHlHr",
                    "userId": "5a50eae4a24c4ebfbdf16b7c537b81aa"
                }
            }
            """.trimIndent()
        ),

        // 注册
        ApiServiceS.BASE_URL_AUTH + "v1/register" to mapOf(
            Regex(".*") to """
            {
                "code": 200,
                "msg": "成功",
                "data": {
                    "accessToken": "zwqGZ3oD1oynglqJOxIJOyxljB5h32PQ7bI8rth5cwMEAmhbWPONxP8p75W3zZaA",
                    "expiresIn": 2592000,
                    "refreshToken": "VCzpo9jNRnXVEhV8gyIko2TAn9qEnGfxAX7yJ8lFsR6gtv1x9oMQEpaQo6rnio5WVuawqPhflMJss0jVhW12OvdPOUEZCrux3gxyCNKn4nBigXUsbCpO38HuDR2o22nr",
                    "userId": "5a50eae4a24c4ebfbdf16b7c537b81aa"
                }
            }
            """.trimIndent()
        ),

        // 刷新 Token
        ApiServiceS.BASE_URL_AUTH + "v1/refresh_token" to mapOf(
            Regex(".*") to """
            {
                "code": 200,
                "msg": "成功",
                "data": {
                    "accessToken": "IszKGVnxUpqvQiovNWt2llk1FzQmeJuSueaquzmP9axyUMMQkFZetTRfvpauJJ5r",
                    "expiresIn": 2592000,
                    "refreshToken": "paNGHEdxo3BZqR6V3is9r82PyOhFqtjQLWKNrkTqFA2JISSe3KDqU9Ac44qI7NJfoyBgoWPP5r2JCBY6uv5HVKzq3XLKsGpoUNxYPJQcOlFaDT9gR8b6mG5RlUZBHlHr",
                    "userId": "5a50eae4a24c4ebfbdf16b7c537b81aa"
                }
            }
            """.trimIndent()
        ),

        // UnifiedAuth（第三方登录）
        ApiServiceS.BASE_URL_AUTH + "v1/authentication/unifiedAuth" to mapOf(
            Regex(".*") to """
            {
                "code": 200,
                "msg": "成功",
                "data": {
                    "type": "unified_auth",
                    "token": "723e5af3de084a86a1fdf8ed771e79a5",
                    "expiresIn": 900
                }
            }
            """.trimIndent()
        ),

        // 获取会话列表
        ApiServiceS.BASE_URL_AI + "user/v1/conversations" to mapOf(
            Regex(".*") to """
            {
              "code": 200,
              "msg": "本地会话列表",
              "data": {
                "page": 1,
                "size": 1,
                "data": [
                  {
                    "conversationId": "mock-id",
                    "title": "本地对话",
                    "createTime": "2025-04-30T16:00:00Z",
                    "updateTime": "2025-04-30T16:05:00Z"
                  }
                ]
              }
            }
            """.trimIndent()
        ),

        // （可选）SSE 对话流：如果也希望通过 MockInterceptor 拦截 SSE
        ApiServiceS.BASE_URL_AI + "user/v1/conversation" to mapOf(
            Regex(".*") to """
            id:mock-1
            event:newConversation
            data:{"type":"newConversation","conversationId":"mock-1","messageId":1,"parentId":0}

            id:mock-2
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"**示例文章标题**"}

            id:mock-3
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"\n*这是一段示例正文"}

            id:mock-4
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"，用于展示斜体效果。"}

            id:mock-5
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"*\n```cpp\ni"}

            id:mock-6
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"nt add(int"}

            id:mock-7
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":" a, int b)"}

            id:mock-8
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":" {\n    ret"}

            id:mock-9
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"urn a + b;"}

            id:mock-10
            event:message
            data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"\n}\n```"}
            """.trimIndent()
        )
    )
}
