mkdir -p domain/src/main/kotlin/com/example/domain/model
mkdir -p domain/src/main/kotlin/com/example/domain/repository
mkdir -p domain/src/main/kotlin/com/example/domain/usecase/list
mkdir -p domain/src/main/kotlin/com/example/domain/usecase/item
mkdir -p domain/src/main/kotlin/com/example/domain/usecase/category
mkdir -p data/src/main/kotlin/com/example/data/db/dao
mkdir -p data/src/main/kotlin/com/example/data/db/entity
mkdir -p data/src/main/kotlin/com/example/data/mapper
mkdir -p data/src/main/kotlin/com/example/data/repository
mkdir -p data/src/main/kotlin/com/example/data/di
mkdir -p core/src/main/kotlin/com/example/core/base
mkdir -p core/src/main/kotlin/com/example/core/util
mkdir -p konsist-tests/src/test/kotlin
touch domain/build.gradle.kts
touch data/build.gradle.kts
touch core/build.gradle.kts
touch konsist-tests/build.gradle.kts
echo "Done!"
