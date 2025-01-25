GET http://localhost:8080/photos

POST http://localhost:8080/photos
[MultipartFormData]
attachment: file,image.png;

DELETE http://localhost:8080/photos/{{id}}
