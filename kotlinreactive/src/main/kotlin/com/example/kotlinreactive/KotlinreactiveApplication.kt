package com.example.kotlinreactive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.reactive.function.server.*

@SpringBootApplication
class KotlinreactiveApplication {

    /* @Bean
     fun routes(cr: CustomerRepository) =
         coRouter {
             GET("/customers/{id}") { serverRequest ->
                 val pathVariable: Int = Integer.parseInt(serverRequest.pathVariable("id"))
                 println( Thread.currentThread().name)
                 val customer: Customer = cr.findById(pathVariable).awaitSingle()
                 println( customer.id)
                 println( Thread.currentThread().name)
                 ServerResponse.ok().bodyValueAndAwait(customer)
             }
             GET("/customers") {
                 val results: Flow<Customer> = cr.findAll().asFlow()
                 ServerResponse.ok().bodyAndAwait(results)
             }
         }*/
}

@Controller
class CustomerRestController(private val customerRepository: CustomerRepository) {

    @GetMapping ("/customers")
    suspend fun customers () = this.customerRepository.findAll().asFlow()

    @GetMapping ("/customers/{id}")
    suspend fun byId (@PathVariable id:Int) = this.customerRepository.findById( id ).awaitSingle()

}

fun main(args: Array<String>) {
    runApplication<KotlinreactiveApplication>(*args)
}


interface CustomerRepository : ReactiveCrudRepository<Customer, Int>
data class Customer(@Id val id: Int, val name: String)