package com.fsa.leaf_logic

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuarioOpenHelper(context: Context) : SQLiteOpenHelper(context, NOME_BANCO_DE_DADOS, null, VERSAO_BANCO_DE_DADOS) {

    companion object {
        private const val NOME_BANCO_DE_DADOS = "usuario.db"
        private const val VERSAO_BANCO_DE_DADOS = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = "CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Seu código de migração do banco de dados aqui
        if (newVersion > oldVersion) {
            // Crie uma nova tabela temporária sem a coluna "age"
            db.execSQL("CREATE TABLE user_temp (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL)")

            // Copie os dados da tabela antiga para a nova tabela
            db.execSQL("INSERT INTO user_temp (name, email, password) SELECT name, email, password FROM user")

            // Exclua a tabela antiga
            db.execSQL("DROP TABLE user")

            // Renomeie a nova tabela para o nome original
            db.execSQL("ALTER TABLE user_temp RENAME TO user")
        }
    }


    fun insertUser(user: User){
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("password", user.password)
        }
            db.insert("user", null, values)
            db.close()
    }

    fun getUserIdByName(email: String): Int? {
        val db = writableDatabase
        val projection = arrayOf("id")
        val selection = "email = ?"
        val selectionArgs = arrayOf(email)

        val cursor = db.query(
            "user",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var userId: Int? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()

        return userId
    }

    fun verificarUsuario(email: String, password: String): Boolean {
        val db = readableDatabase
        val colunas = arrayOf("id")
        val selecao = "email = ? AND password = ?"
        val selecaoArgs = arrayOf(email, password)
        val cursor: Cursor = db.query("user", colunas, selecao, selecaoArgs, null, null, null)
        val usuarioExiste = cursor.count > 0
        cursor.close()
        return usuarioExiste
    }
}