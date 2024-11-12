package com.fsa.leaf_logic

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // ============ NOTIFICACOES ============
    @GET("api/Notificacao/GetNotificacoesConcluidas/{plantaId}") // Rota da API
    suspend fun getNotificacoesPorPlantaConcluida(
        @Path("plantaId") plantaId: String
    ): List<Notificacao>

    @GET("api/Notificacao/GetNotificacoesPendentes/{plantaId}") // Rota da API
    suspend fun getNotificacoesPorPlantaPendente(
        @Path("plantaId") plantaId: String
    ): List<Notificacao>

    @PUT("api/Notificacao/concluirNotificacao/{id}") // Rota da API
    suspend fun concluirNotificacao(
        @Path("id") id: String
    ): Response<Notificacao>

    // ============ END NOTIFICACOES ============

    // ============ EQUIPAMENTOS ============
    @GET("api/Equipamento/GetEquip/{equipamentoName}") // Rota da API
    suspend fun getEquipamentoPorNome(
        @Path("equipamentoName") equipamentoName: String
    ): Response<Equipamento>

    // ============ END EQUIPAMENTOS ============

    // ============ PLANTAS ============
    @GET("api/Planta/GetPlantas/{userId}") // Rota da API
    suspend fun getPlantasPorUserId(
        @Path("userId") userId: String?
    ): List<Planta>

    @POST("api/Planta")
    suspend fun criarPlanta(@Body planta: Planta): Response<Planta>

    // ============ END PLANTAS ============

    // ============ LEITURAS ============
    @GET("api/Leitura") // Rota da API
    suspend fun getLeituras(): List<Leitura>

    @GET("api/Leitura/{equipamentoId}/{inicio}/{fim}")
    suspend fun getLeiturasByEquipamentoNData(
        @Path("equipamentoId") equipamentoId: String,
        @Path("inicio") inicio: String, // Formato yyyy-MM-dd
        @Path("fim") fim: String        // Formato yyyy-MM-dd
    ): List<Leitura>

    // ============ END LEITURAS ============

    // ============ USUÁRIOS ============
    @GET("api/Usuarios")
    suspend fun getUsuarios(): Response<List<User>>

    @GET("api/Usuarios/{email}/{pass}")
    suspend fun getUsuarioByEmailNPass(
        @Path("email") email: String,
        @Path("pass") pass: String
    ): Response<User>

    @GET("api/Usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Int): Response<User>

    @POST("api/Usuarios")
    suspend fun criarUsuario(@Body usuario: User): Response<User>

    @PUT("api/Usuarios/{id}")
    suspend fun atualizarUsuario(@Path("id") id: Int, @Body usuario: User): Response<Void>

    @DELETE("api/Usuarios/{id}")
    suspend fun deletarUsuario(@Path("id") id: Int): Response<Void>
    // ============ END USUÁRIOS ============
}