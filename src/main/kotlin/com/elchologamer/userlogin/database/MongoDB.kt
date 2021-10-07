package com.elchologamer.userlogin.database

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import org.bson.conversions.Bson
import java.util.*

class MongoDB : Database() {
    private var client: MongoClient? = null
    private var db: MongoDatabase? = null
    private var collection: MongoCollection<Document>? = null

    override fun connect() {
        // Get config options
        val section = plugin.config.getConfigurationSection("database.mongodb")
            ?: throw RuntimeException("MongoDB configuration missing in config.yml")

        val uri = section.getString("uri", "mongodb://localhost:27019")!!
        val dbName = section.getString("database", "userlogin")!!
        val collectionName = section.getString("collection", "players")!!

        // Connect with URI
        val mongoURI = MongoClientURI(uri)
        client = MongoClient(mongoURI)
        db = (client ?: return).getDatabase(dbName)
        collection = (db ?: return).getCollection(collectionName)
    }

    override fun getRawPassword(uuid: UUID): String {
        val filter = Filters.eq("_id", uuid.toString())
        val doc = collection?.find(filter)?.first() ?: throw IllegalArgumentException()

        return doc.getString("password")
    }

    override fun createRawPassword(uuid: UUID, password: String) {
        val doc = Document("_id", uuid.toString()).append("password", password)
        collection?.insertOne(doc) ?: throw IllegalStateException("MongoDB collection is unitialized")
    }

    override fun updateRawPassword(uuid: UUID, password: String) {
        val filter = Filters.eq("_id", uuid.toString())
        val update: Bson = Document("\$set", Document("password", password))
        collection!!.updateOne(filter, update)
    }

    override fun deletePassword(uuid: UUID) {
        val filter = Filters.eq("_id", uuid.toString())
        collection?.deleteOne(filter) ?: throw IllegalStateException("MongoDB collection is unitialized")
    }

    override fun disconnect() {
        client?.close()
    }
}