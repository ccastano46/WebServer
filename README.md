# WebFramework

WebFramework is a lightweight Java framework that recognizes and processes REST annotations (`@RestController`, `@GetMapping`, and `@RequestParam`) to build and handle HTTP requests dynamically. It uses reflection to scan annotated classes, register endpoints, and execute mapped methods with request parameters.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

To run this project, you need to install Java and Maven. Follow the instructions below for your operating system.

**Java 11 or higher**

Visit [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/) and download Java Development Kit (JDK) 11 or higher.

**Installation on Linux/Mac:**

```bash
# Download the JDK installer and follow the official Oracle instructions
# For macOS with Homebrew:
brew install openjdk@11

# Verify installation
java -version
```

**Maven 3.6 or higher**

Visit [Apache Maven](https://maven.apache.org) and download Maven 3.6 or higher.

**Installation on Linux/Mac:**

```bash
# Download Maven from https://maven.apache.org/download.cgi
# Extract and set up environment variables

# For macOS with Homebrew:
brew install maven

# Verify installation
mvn --version
```

### Installing

A step by step series of examples to get a development environment running:

**Step 1: Clone the repository**

```bash
git clone https://github.com/ccastano46/WebServer.git
cd WebServer
```

**Step 2: Build the project with Maven**

```bash
mvn clean compile
```

**Step 3: Verify the build**

```bash
ls -la target/classes/
```

The compiled classes should be available in the `target/classes/` directory.

## Deployment

This section provides step-by-step instructions to deploy WebFramework on AWS EC2.

### Step 1: Create an AWS EC2 Instance

**1. Navigate to EC2 Dashboard**

Log in to your AWS account and go to the EC2 service.

**2. Select Amazon Linux 2023 AMI**

Choose "Amazon Linux 2023 (kernel-6.1)" as shown in the AWS console:

![Select AMI](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfWmp1TkpvX2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmV21wMVRrcHZYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=HIIpxosCpFONYO4sC-9yOgRe4pIXnDRYNNSwkQAG-X2UTYQSfzC4lcM01uAMiO1m8ewnR9AdJ~PTifDdrjCt-DbJtj6LVojNqD7Lm-A4nM~-1dyVGA~wsTi96X~26res0lz5543pmg8EGiBGU52P7QYnSQc-rsr401tDl8xHyNTky0Ncp~do-ZM5uJjLcufvhBiaTrJjfMET8IUynyUd~P8SyMRxUueiS5Y7hVsakiQMiwpbXWx-MteJnJ97uJdTRGHpBJCdeNqnGZNXywV1TJTtuXosEe7N7KVJUqEiNY0fxC8t1zgjMy7DBW4LyOlgjjeI1ec9SimJrZrrL7w~aA__)

**3. Configure Instance Details**

Set up your instance with the following configuration:

- **Instance Type:** t2.micro (eligible for free tier)
- **Storage:** 8 GiB GP3 volume with 3000 IOPS
- **Network:** Default VPC
- **Subnet:** Default subnet with auto-assign public IP enabled

![Configure Storage](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfdjRrOXhDX2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmZGpSck9YaERYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=NaHVA1YD4e2TyIcOEgwDkH2MGKOQV5GrQrfRGBocPtxa-B7BmbSAuNOKo0NepRtjZqnY~GGS4xL0hDcXeFyVaUMO4cH-A-veENQS~b6okJgjlaU5flYNokPxDhF7R5km8WsfV~feUSZttXio61fDUi7Zhl2~WJka0PdpUCEfd1zpPwdfDCamgmM5mh-sGS~o-4G-yAy9m~jPe6fk6LvKt5oK957NUJ780r5488J-1ll3zRllpGlV2H77N-lTePKHsBqPMKwGBNs4V39ISJm7UVhWLbC-qO20OVLvLya~M9RFeO-2TAsc9IatIzAaa8qKEC-dCsH2KJiANpIB2GQUaA__)

**4. Configure Security Groups**

Create a new security group with the following rules:

- **SSH access:** Allow SSH traffic from anywhere (0.0.0.0/0) for console access
- **Note:** For production, restrict SSH access to known IP addresses only

![Security Group Configuration](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfYlFzRlZDX2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmWWxGelJsWkRYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=Ge1eYCYlMeQQQ17ZIuoDphw-WMlMXEA0nnWkYEg3KdDOstV7XHnuNw8Vn8DBVF5DRQqqxHI7BRPtUI8W93v8qXR7HSw4f~PHKxwB3W2Yy-JEq5ATfE8pSFn8oha1U5yiTSzllH5mMtAcuvgn04qXuHUGcUVk6osULtilc7RZsFUsEptzOC1ik0AlY20UMDfhP8x6e4GUIuaOZdnfukOCY9Gx-ZxSbuRc2jd4Fn8SBdK-dQ6jdRuBL-urcP~jjrcLurc1LD62ciJ-TX8E1YbzTRu80OKwhYxZvQViF5IY2fhNS~uSf8QwFTsphota8cjRAPKQ9GQDGo0tX4TD5hgUhg__)

**5. Create or Select Key Pair**

Create a new key pair (e.g., `castanoqECI.pem`) for secure SSH access:

![Key Pair Setup](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfcnVkMGtTX2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmY25Wa01HdFRYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=khcizTZXPreRCta6juli88qldw1cQZ4ngxWNY7TOICoUtrEeAxBEaqv-gBm0~F4BR06gD1E2f65Oc8G9N6tJVcZM1yUzV135UnvZxXYz~72YSqUZtinorpikNyLE0fJQSqzfwJWdHT1uRQ8bdijExl~X7UnFhI5zFA-NbV2XjFRDdPb3RLk4fZQpuBn6SQFIGzz7aA1UylZZFIQjnvZPFKdLBLpdQJ8YKEE2hWHkPAtQczYDe6GTzW06TGsSx2DSb3edcD93WphnljJ9nF4L6Vo-o8bMxmd4tj9PrTbvGAGXeqc~WOSHVrgZiITiMtxo-dQ02Vx~WFuNlQe6euUp-g__)

**6. Launch the Instance**

Click "Launch Instance" and wait for the instance to be running.

### Step 2: Connect to Your EC2 Instance

Once your instance is running, connect via SSH using the key pair you created.

**On Linux or macOS:**

```bash
# Set appropriate permissions on your key file
chmod 400 castanoqECI.pem

# Connect to the instance (replace PUBLIC_DNS with your instance's public DNS)
ssh -i "castanoqECI.pem" ec2-user@PUBLIC_DNS
```

**Example:**

```bash
ssh -i "castanoqECI.pem" ec2-user@ec2-54-123-45-67.compute-1.amazonaws.com
```

### Step 3: Install Required Software on EC2

Once connected to your EC2 instance, install Java, Git, and Maven:

**Install Java 21 (Amazon Corretto)**

```bash
sudo yum install java-21-amazon-corretto-devel
```

**Install Git**

```bash
sudo yum install git
```

**Install Maven**

```bash
sudo yum install maven
```

**Verify installations:**

```bash
java -version
git --version
mvn --version
```

### Step 4: Clone and Build the Project

```bash
# Clone the repository
git clone https://github.com/ccastano46/WebServer.git
cd WebServer

# Build the project
mvn clean compile
```

### Step 5: Run the Application

Execute the WebFramework application:

```bash
java -cp target/classes org.example.runner.WebServerMain
```

**Expected output:**

The application will scan for all `@RestController` classes, register endpoints, and display an interactive menu:

![Application Running on EC2](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfcmpiZFRWX2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmY21waVpGUldYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=Btjvufws65zIb1GumskOLE~fuPFIU1kSW5kaRVaLz6K~ADf2b5rLbv-GyTGUMAgqCT3oigNacSBq73iLQSmrBKKLooyvszs0Wis7YKQNtp6BeFSxS9pfhXFbCUG8m2vttQXehOdO9sEocvM-l0g1AgBG8dnBGAHg8p944oIdxvTSox21tb9KFFSZUSTCFIM8i3jxvhdfVTopYW6h-PVabAJkRtDKTf4SYf7RH5g5uRSZzyc2P7xfRMRWh9UU4QFTqeSOHY5vLiEaUsW8cI9hMkljm-NtHvX9scknh1lytqOC3dJNoXG99~w0WA0RkQu0-syBj3G0FUFA2SZ5sNqCyA__)

**Interactive testing:**

Select an endpoint from the menu and provide parameter values. The framework will invoke the corresponding method and display the result:

![Application Response](https://private-us-east-1.manuscdn.com/sessionFile/NVgYEupGFxczwQKcBxt84q/sandbox/nDIuNq9rj1z2jxHI3vtZAH-images_1773399271865_na1fn_L2hvbWUvdWJ1bnR1L1dlYkZyYW1ld29ya19SRUFETUUvcGFzdGVkX2ZpbGVfV0JlcW94X2ltYWdl.png?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiaHR0cHM6Ly9wcml2YXRlLXVzLWVhc3QtMS5tYW51c2Nkbi5jb20vc2Vzc2lvbkZpbGUvTlZnWUV1cEdGeGN6d1FLY0J4dDg0cS9zYW5kYm94L25ESXVOcTlyajF6Mmp4SEkzdnRaQUgtaW1hZ2VzXzE3NzMzOTkyNzE4NjVfbmExZm5fTDJodmJXVXZkV0oxYm5SMUwxZGxZa1p5WVcxbGQyOXlhMTlTUlVGRVRVVXZjR0Z6ZEdWa1gyWnBiR1ZmVjBKbGNXOTRYMmx0WVdkbC5wbmciLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE3OTg3NjE2MDB9fX1dfQ__&Key-Pair-Id=K2HSFNDJXOU9YS&Signature=SbA6SIdaZv-3M5PVz3s-ZNVRgLUeqha0t0tkVow1gIWY6ph9kP6g8oDzztsArNjHbYLGe39Zhzp6X0SYCfoHjSx3ZeecMmHyDalm8btOUxVRV3q~teRAfvKnDU9AUT6I2C6re6ZDLHkQhCpsJKUAq4UNyJ60aL65D3wj52pn38TBx-onGkY~X21exTGqofhSlH1AGP7PxTerTOstJm7W0cwNvD~X6fyyqXhoJVso2udi1MHfnQhCCPwCxCA0cbbl8UfKWlRM7tcRdeCdMvw3mSev68edhWnOIy5pfBIpmgPOJr5-mv2unu-AhRTKM9Bch3pdHMwA6UV4Wwvz9xR2BQ__)

## Built With

* [Java](https://www.oracle.com/java/) - Programming language
* [Maven](https://maven.apache.org/) - Dependency management and build tool
* [Reflection API](https://docs.oracle.com/javase/tutorial/reflect/) - Dynamic class scanning and method invocation

## Authors

* **Camilo Castano** - *Initial work* - [GitHub](https://github.com/ccastano46)

## Acknowledgments

* Built with Java Reflection API for dynamic annotation processing
* Inspired by popular REST frameworks like Spring Framework
