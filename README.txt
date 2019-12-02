1.aktualny format zwracanych WSZYSTKICH itemów (testów) z tabeli przy użyciu API z HTTP GET

responseText: "[
  {
    "max_points": 3,
    "min_points": 2,
    "questions": [
      {
        "question_type": "W",
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
        "question_content": "Czy Ziemia jest płaska?"
      },
      {
        "question_type": "L",
        "correct_answer": 8,
        "question_content": "Ile wynosi (2+2)*2?"
      },
      {
        "question_type": "O",
        "question_content": "Opisać zjawisko indukcji elektromagnetycznej."
      }
    ],
    "recruiter_id": "another_tester22112019",
    "test_id": "test1911300924341134"
  },
  {
    "max_points": 3,
    "min_points": 2,
    "questions": [
      {
        "question_type": "W",
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
        "question_content": "Czy Ziemia jest płaska?"
      },
      {
        "question_type": "L",
        "correct_answer": 8,
        "question_content": "Ile wynosi (2+2)*2?"
      },
      {
        "question_type": "O",
        "question_content": "Opisać zjawisko indukcji elektromagnetycznej."
      }
    ],
    "recruiter_id": "tester22112019",
    "test_id": "test1911300915571157"
  },
  {
    "max_points": 3,
    "min_points": 2,
    "questions": [
      {
        "question_type": "O",
        "question_content": "Rozwiąż zadanie [treść]."
      },
      {
        "question_type": "W",
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
        "question_content": "Czy Ziemia jest płaska?"
      },
      {
        "question_type": "L",
        "correct_answer": 8,
        "question_content": "Ile wynosi (2+2)*2?"
      },
      {
        "question_type": "W",
        "answers": [
          {
            "answer": "1880",
            "correct": false
          },
          {
            "answer": "1892",
            "correct": false
          },
          {
            "answer": "1889",
            "correct": true
          }
        ],
        "question_content": "W którym roku urodził się Adolf Hitler?"
      },
      {
        "question_type": "O",
        "question_content": "Opisać zjawisko indukcji elektromagnetycznej."
      },
      {
        "question_type": "L",
        "correct_answer": 64,
        "question_content": "Ile wynosi 8*8?"
      }
    ],
    "recruiter_id": "tester22112019",
    "test_id": "test221120191222"
  }
]"

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