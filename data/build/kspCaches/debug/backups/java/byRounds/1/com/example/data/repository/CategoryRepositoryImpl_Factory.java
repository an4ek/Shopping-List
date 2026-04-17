package com.example.data.repository;

import com.example.data.db.dao.CategoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class CategoryRepositoryImpl_Factory implements Factory<CategoryRepositoryImpl> {
  private final Provider<CategoryDao> daoProvider;

  public CategoryRepositoryImpl_Factory(Provider<CategoryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CategoryRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static CategoryRepositoryImpl_Factory create(Provider<CategoryDao> daoProvider) {
    return new CategoryRepositoryImpl_Factory(daoProvider);
  }

  public static CategoryRepositoryImpl newInstance(CategoryDao dao) {
    return new CategoryRepositoryImpl(dao);
  }
}
