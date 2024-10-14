# Project Instructions

## Compiling the Binaries

To compile the project binaries, use **Gradle**'s `installDist` task, which packages the project and prepares everything for distribution:

`./gradlew installDist`

This will generate the distribution files (binaries, dependencies, and scripts) in the `build/install/project2_q2` directory.

---

## Starting the Server

To start the server, use the following command:

`./build/install/project2_q2/bin/age-calculator-server`

This starts the age calculator server, which the client will interact with to process the input age and return the appropriate output.

---

### What Does the Client Do?

To understand what the client can do or to view available commands and options, run the following help command:

`./build/install/project2_q2/bin/age-calculator-client --help`

This will display helpful information about the client, including the available commands, options, and usage.

---

## Running the Client

To run the client and get the output based on a given integer age, use the following command:

`./build/install/project2_q2/bin/age-calculator-client <Int-Age>`

- Replace `<Int-Age>` with an integer value representing the age you want to process.  
  For example:

  `./build/install/project2_q2/bin/age-calculator-client 25`
---


## Cleaning the Build Directory

To clean up the `build` folder and remove previously generated files, you can use the following Gradle command:

`gradle clean`

This will remove all build artifacts, ensuring a fresh build the next time you run the compilation commands.

---

### Summary of Commands

| Action                          | Command                                                       |
|----------------------------------|---------------------------------------------------------------|
| Run the client                   | `./build/install/project2_q2/bin/age-calculator-client <Int-Age>` |
| Get help for the client          | `./build/install/project2_q2/bin/age-calculator-client --help`    |
| Start the server                 | `./build/install/project2_q2/bin/age-calculator-server`           |
| Compile the binaries             | `./gradlew installDist`                                          |
| Clean the build folder           | `gradle clean`                                                   |

---
