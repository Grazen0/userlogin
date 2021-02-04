package com.elchologamer.userlogin.util.database;

import com.elchologamer.userlogin.util.Utils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.configuration.ConfigurationSection;

import java.net.UnknownHostException;
import java.util.UUID;

public class MongoDB implements Database {

    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    @Override
    public void connect() throws UnknownHostException {
        // Get config options
        ConfigurationSection section = Utils.getConfig().getConfigurationSection("database.mongodb");
        assert section != null;

        String uri = section.getString("uri");
        String dbName = section.getString("database", "userlogin");
        String collectionName = section.getString("collection", "players");

        // Connect with URI
        MongoClientURI mongoURI = new MongoClientURI(uri);
        client = new MongoClient(mongoURI);

        db = client.getDatabase(dbName);
        collection = db.getCollection(collectionName);
    }

    @Override
    public String getPassword(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        Document doc = collection.find(filter).first();
        if (doc == null) return null;

        return doc.getString("password");
    }

    @Override
    public void setPassword(UUID uuid, String password) {
        Bson filter = Filters.eq("_id", uuid.toString());
        Bson update = new Document("$set", new Document("password", password));
        UpdateOptions options = new UpdateOptions().upsert(true);

        collection.updateOne(filter, update, options);
    }

    @Override
    public void deletePassword(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        collection.deleteOne(filter);
    }

    @Override
    public void disconnect() {
        if (client != null) client.close();
    }
}
