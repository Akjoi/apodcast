package com.example.apodcast

import com.example.apodcast.data.player.IPlayerRepository
import com.example.apodcast.data.player.PlayerRepository
import com.example.apodcast.ui.main.MainScreenViewModel
import com.example.apodcast.ui.player.PlayerControllerViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Component(modules = [AppModule::class])
interface AppComponent {
//    fun inject(playerControllerViewModel: PlayerControllerViewModel)
    fun inject(mainScreenViewModel: MainScreenViewModel)
}

@Module(includes = [NetworkModule::class, AppBindsModule::class])
class AppModule {}

@Module
class NetworkModule {

    @Provides
    fun provideApi(
        okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory
    ): Api {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://storage.googleapis.com/")
            .addConverterFactory(gsonConverterFactory)
            .build()
        return retrofit.create(Api::class.java)
    }

    @Provides
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun gson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().build()
    }
}

@Module
interface AppBindsModule {

    @Binds
    fun bindPlayerRepository(playerRepository: PlayerRepository): IPlayerRepository
}