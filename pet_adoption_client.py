import grpc
import pet_adoption_pb2
import pet_adoption_pb2_grpc


def register_pet(stub):
    # Sample pet data
    pet_name = "Poodle"
    pet_gender = "Male"
    pet_age = 2
    pet_breed = "Dog"

    # For simplicity, we'll send a dummy picture (in practice, you'd load from a file)
    pet_picture = b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR'  # Dummy image data

    # Create a pet object
    pet = pet_adoption_pb2.Pet(
        name=pet_name,
        gender=pet_gender,
        age=pet_age,
        breed=pet_breed,
        picture=pet_picture
    )

    # Send the request to the server
    response = stub.RegisterPet(pet)

    # Display the server response
    if response.success:
        print(f"Pet '{pet_name}' registered successfully!")
    else:
        print(f"Failed to register pet: {response.message}")


def search_pet(stub):
    keyword = "Poodle"

    # Create a search request
    search_request = pet_adoption_pb2.SearchRequest(keyword=keyword)

    # Send the request to the server
    response = stub.SearchPet(search_request)

    # Display the search results
    if len(response.pets) > 0:
        for pet in response.pets:
            print(f"Name: {pet.name}, Gender: {pet.gender}, Age: {pet.age}, Breed: {pet.breed}")
    else:
        print("No pets found.")


def run():
    # Establish a channel with the gRPC server
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = pet_adoption_pb2_grpc.PetAdoptionServiceStub(channel)

        while True:
            print("1. Register a new pet")
            print("2. Search for a pet")
            print("3. Exit")
            choice = input("Enter choice: ")

            if choice == "1":
                register_pet(stub)
            elif choice == "2":
                search_pet(stub)
            elif choice == "3":
                break
            else:
                print("Invalid choice. Try again.")


if __name__ == "__main__":
    run()
