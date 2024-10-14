package com.petadoption;

import io.grpc.stub.StreamObserver;
import com.petadoption.PetAdoptionProto.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class PetAdoptionServiceImpl extends PetAdoptionServiceGrpc.PetAdoptionServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(PetAdoptionServiceImpl.class);
    private final MongoCollection<Document> petCollection;

    // Constructor to initialize MongoDB connection
    public PetAdoptionServiceImpl() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("petAdoptionDB");
        petCollection = database.getCollection("pets");
    }

    // Register a new pet
    @Override
    public void registerPet(RegisterPetRequest request, StreamObserver<RegisterPetResponse> responseObserver) {
        logger.info("Registering a new pet: " + request.getName());

        // Convert the gRPC request into a MongoDB document
        Document petDoc = new Document("name", request.getName())
                .append("gender", request.getGender())
                .append("age", request.getAge())
                .append("breed", request.getBreed())
                .append("picture", request.getPicture().toByteArray());

        // Insert the document into MongoDB
        petCollection.insertOne(petDoc);

        // Build and send the response
        RegisterPetResponse response = RegisterPetResponse.newBuilder()
                .setMessage("Pet registered successfully!")
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Search for a pet by one or more key details (e.g., name, gender, age, breed)
    @Override
    public void searchPet(SearchPetRequest request, StreamObserver<SearchPetResponse> responseObserver) {
        logger.info("Searching for pets with the following criteria - Name: {}, Gender: {}, Age: {}, Breed: {}",
                request.getName(), request.getGender(), request.getAge(), request.getBreed());

        // Build MongoDB search query
        List<Bson> filters = new ArrayList<>();
        if (!request.getName().isEmpty()) {
            filters.add(eq("name", request.getName()));
        }
        if (!request.getGender().isEmpty()) {
            filters.add(eq("gender", request.getGender()));
        }
        if (request.getAge() >= 0) {
            filters.add(eq("age", request.getAge()));
        }
        if (!request.getBreed().isEmpty()) {
            filters.add(eq("breed", request.getBreed()));
        }

        Bson query = filters.isEmpty() ? new Document() : and(filters);  // Match all if no filters are provided

        List<Pet> pets = new ArrayList<>();
        for (Document doc : petCollection.find(query)) {
            Pet pet = Pet.newBuilder()
                    .setPetId(doc.getObjectId("_id").toString())
                    .setName(doc.getString("name"))
                    .setGender(doc.getString("gender"))
                    .setAge(doc.getInteger("age"))
                    .setBreed(doc.getString("breed"))
                    .setPicture(com.google.protobuf.ByteString.copyFrom(doc.get("picture", byte[].class)))
                    .build();
            pets.add(pet);
        }

        // Build and send the response
        SearchPetResponse response = SearchPetResponse.newBuilder()
                .addAllPets(pets)
                .setMessage(pets.isEmpty() ? "No pets found" : pets.size() + " pet(s) found")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Update details of an existing pet
    @Override
    public void updatePetDetails(UpdatePetRequest request, StreamObserver<UpdatePetResponse> responseObserver) {
        logger.info("Updating pet with ID: " + request.getPetId());

        Document updatedPet = new Document();
        if (!request.getName().isEmpty()) {
            updatedPet.append("name", request.getName());
        }
        if (!request.getGender().isEmpty()) {
            updatedPet.append("gender", request.getGender());
        }
        if (request.getAge() >= 0) {
            updatedPet.append("age", request.getAge());
        }
        if (!request.getBreed().isEmpty()) {
            updatedPet.append("breed", request.getBreed());
        }
        if (request.getPicture() != null) {
            updatedPet.append("picture", request.getPicture().toByteArray());
        }

        Document updateOperation = new Document("$set", updatedPet);
        petCollection.updateOne(eq("_id", request.getPetId()), updateOperation);

        // Build and send the response
        UpdatePetResponse response = UpdatePetResponse.newBuilder()
                .setMessage("Pet details updated successfully!")
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Delete a pet by its unique ID
    @Override
    public void deletePet(DeletePetRequest request, StreamObserver<DeletePetResponse> responseObserver) {
        logger.info("Deleting pet with ID: " + request.getPetId());

        petCollection.deleteOne(eq("_id", request.getPetId()));

        // Build and send the response
        DeletePetResponse response = DeletePetResponse.newBuilder()
                .setMessage("Pet deleted successfully!")
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Get detailed information of a specific pet by its unique ID
    @Override
    public void getPetDetails(GetPetRequest request, StreamObserver<GetPetResponse> responseObserver) {
        logger.info("Fetching details for pet with ID: " + request.getPetId());

        Document doc = petCollection.find(eq("_id", request.getPetId())).first();
        if (doc == null) {
            // If pet not found, return a not found response
            GetPetResponse response = GetPetResponse.newBuilder()
                    .setMessage("Pet not found")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // Build the response with pet details
        Pet pet = Pet.newBuilder()
                .setPetId(doc.getObjectId("_id").toString())
                .setName(doc.getString("name"))
                .setGender(doc.getString("gender"))
                .setAge(doc.getInteger("age"))
                .setBreed(doc.getString("breed"))
                .setPicture(com.google.protobuf.ByteString.copyFrom(doc.get("picture", byte[].class)))
                .build();

        GetPetResponse response = GetPetResponse.newBuilder()
                .setPet(pet)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}