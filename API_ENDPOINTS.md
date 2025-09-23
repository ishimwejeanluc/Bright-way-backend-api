# API Endpoints Cheat Sheet

## SchoolController

### Create School
`POST /api/schools/create`
```json
{
  "name": "Brightway High",
  "region": "East",
  "address": "123 Main St",
  "type": "SECONDARY"
}
```

### Get School by ID
`GET /api/schools/{id}`

### Get All Schools
`GET /api/schools`

### Update School
`PUT /api/schools/{id}`
```json
{
  "name": "Brightway High Updated",
  "region": "West",
  "address": "456 New St",
  "type": "PRIMARY"
}
```

### Delete School
`DELETE /api/schools/{id}`

---

## TeacherController

### Create Teacher
`POST /api/teachers/create`
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "password": "securePassword123",
  "phone": "1234567890",
  "schoolId": "SCHOOL_UUID_HERE",
  "specialization": "Mathematics",
  "courses": [
    { "id": "COURSE_UUID_1" },
    { "id": "COURSE_UUID_2" }
  ]
}
```

### Get Teacher by ID
`GET /api/teachers/{id}`

### Get All Teachers
`GET /api/teachers`

### Update Teacher
`PUT /api/teachers/{id}`
```json
{
  "name": "Jane Doe Updated",
  "email": "jane.doe@example.com",
  "password": "newPassword456",
  "phone": "0987654321",
  "schoolId": "SCHOOL_UUID_HERE",
  "specialization": "Physics",
  "courses": [
    { "id": "COURSE_UUID_3" }
  ]
}
```

### Delete Teacher
`DELETE /api/teachers/{id}`

---

## CourseController

### Create Course
`POST /api/courses/create`
```json
{
  "name": "Mathematics",
  "description": "Advanced math course"
}
```

### Get Course by ID
`GET /api/courses/{id}`

### Get All Courses
`GET /api/courses`

### Update Course
`PUT /api/courses/{id}`
```json
{
  "name": "Mathematics Updated",
  "description": "Updated description"
}
```

### Delete Course
`DELETE /api/courses/{id}`

---

Replace `{id}`, `SCHOOL_UUID_HERE`, and `COURSE_UUID_X` with actual UUIDs from your database.
