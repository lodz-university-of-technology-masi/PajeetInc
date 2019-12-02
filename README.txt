1.aktualny format zwracanych WSZYSTKICH itemów (testów) z tabeli przy użyciu API z HTTP GET

responseText: "{\n  \"max_points\" : 3,\n  \"min_points\" : 2,\n  \"questions\" : [ {\n    \"question_type\" : \"W\",\n    \"answers\" : [ {\n      \"answer\" : \"tak\",\n      \"correct\" : false\n    }, {\n      \"answer\" : \"nie\",\n      \"correct\" : true\n    } ],\n    \"question_content\" : \"Czy Ziemia jest płaska?\"\n  }, {\n    \"question_type\" : \"L\",\n    \"correct_answer\" : 8,\n    \"question_content\" : \"Ile wynosi (2+2)*2?\"\n  }, {\n    \"question_type\" : \"O\",\n    \"question_content\" : \"Opisać zjawisko indukcji elektromagnetycznej.\"\n  } ],\n  \"recruiter_id\" : \"another_tester22112019\",\n  \"test_id\" : \"test1911300924341134\"\n},{\n  \"max_points\" : 3,\n  \"min_points\" : 2,\n  \"questions\" : [ {\n    \"question_type\" : \"W\",\n    \"answers\" : [ {\n      \"answer\" : \"tak\",\n      \"correct\" : false\n    }, {\n      \"answer\" : \"nie\",\n      \"correct\" : true\n    } ],\n    \"question_content\" : \"Czy Ziemia jest płaska?\"\n  }, {\n    \"question_type\" : \"L\",\n    \"correct_answer\" : 8,\n    \"question_content\" : \"Ile wynosi (2+2)*2?\"\n  }, {\n    \"question_type\" : \"O\",\n    \"question_content\" : \"Opisać zjawisko indukcji elektromagnetycznej.\"\n  } ],\n  \"recruiter_id\" : \"tester22112019\",\n  \"test_id\" : \"test1911300915571157\"\n},{\n  \"max_points\" : 3,\n  \"min_points\" : 2,\n  \"questions\" : [ {\n    \"question_type\" : \"O\",\n    \"question_content\" : \"Rozwiąż zadanie [treść].\"\n  }, {\n    \"question_type\" : \"W\",\n    \"answers\" : [ {\n      \"answer\" : \"tak\",\n      \"correct\" : false\n    }, {\n      \"answer\" : \"nie\",\n      \"correct\" : true\n    } ],\n    \"question_content\" : \"Czy Ziemia jest płaska?\"\n  }, {\n    \"question_type\" : \"L\",\n    \"correct_answer\" : 8,\n    \"question_content\" : \"Ile wynosi (2+2)*2?\"\n  }, {\n    \"question_type\" : \"W\",\n    \"answers\" : [ {\n      \"answer\" : \"1880\",\n      \"correct\" : false\n    }, {\n      \"answer\" : \"1892\",\n      \"correct\" : false\n    }, {\n      \"answer\" : \"1889\",\n      \"correct\" : true\n    } ],\n    \"question_content\" : \"W którym roku urodził się Adolf Hitler?\"\n  }, {\n    \"question_type\" : \"O\",\n    \"question_content\" : \"Opisać zjawisko indukcji elektromagnetycznej.\"\n  }, {\n    \"question_type\" : \"L\",\n    \"correct_answer\" : 64,\n    \"question_content\" : \"Ile wynosi 8*8?\"\n  } ],\n  \"recruiter_id\" : \"tester22112019\",\n  \"test_id\" : \"test221120191222\"\n}"

======================================================================================================================================================

2.aktualny format wrzucanych danych do tabeli przy użyciu API z HTTP POST

{
  "maxPoints": 3,
  "minPoints": 2,
  "questions": [
    {
      "answers": [
        {
          "answer": "tak",
          "correct": false
        },
        {
          "answer": "nie",
          "correct": true
        }
      ],
      "content": "Czy Ziemia jest płaska?",
      "type": "W"
    },
    {
      "correctAnswer": 8,
      "content": "Ile wynosi (2+2)*2?",
      "type": "L"
    },
    {
      "content": "Opisać zjawisko indukcji elektromagnetycznej.",
      "type": "O"
    }
  ],
  "recruiterId": "tester22112019",
  "testId": "test221120191222",
  "testName": "test wiedzy ogólnej"
}

======================================================================================================================================================

3.w API Mapping Template to wygląda tak:

{
    "recruiterId": $input.json('$.recruiter-id'),
    "testName": $input.json('$.test-name'),
    "minPoints": $input.json('$.min-points'),
    "maxPoints": $input.json('$.max-points'),
    "questions": $input.json('$.questions')
}