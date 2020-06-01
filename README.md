# student-service

//Add a student
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/students/create' --data '{"name":"roger", "age":25}'
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/students/create' --data '{"name":"sanjay", "age":36}'
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/students/create' --data '{"name":"amit"}'

// Get All students
curl -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/students'

// Get Students By id
curl -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/student/1'

// Get Students by name
curl -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/student/sanjay'


//Remove student
curl -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i 'http://localhost:8081/services/api/student/remove/1'

