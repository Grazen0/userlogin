package com.elchologamer.userlogin.database;

import com.elchologamer.userlogin.UserLogin;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MongoDB extends Database {

    private final JavaPlugin plugin = UserLogin.getPlugin();
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> collection;

    @Override
    public void connect() {
        // Get config options
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database.mongodb");
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
    public String getRawPassword(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        Document doc = collection.find(filter).first();
        if (doc == null) return null;

        return doc.getString("password");
    }

    @Override
    public void createRawPassword(UUID uuid, String password) {
        Document doc = new Document("_id", uuid.toString()).append("password", password);
        collection.insertOne(doc);
    }

    @Override
    public void updateRawPassword(UUID uuid, String password) {
        Bson filter = Filters.eq("_id", uuid.toString());
        Bson update = new Document("$set", new Document("password", password));

        collection.updateOne(filter, update);
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
