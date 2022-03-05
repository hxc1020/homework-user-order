package com.thoughtworks.userorder.repository

import org.junit.ClassRule
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=" + mysql.jdbcUrl,
            "spring.datasource.username=" + mysql.username,
            "spring.datasource.password=" + mysql.password
        ).applyTo(applicationContext.environment)
    }
}

@ClassRule
val mysql = MySQLContainer<Nothing>(DockerImageName.parse("mysql:8.0.27")).apply {
    withDatabaseName("user_order")
    withInitScript("../test/sql/order/init_order.sql")
    start()
}

@Throws(SQLException::class)
fun performQuery(ds: DataSource, sql: String) {
    val statement: Statement = ds.connection.createStatement()
    statement.execute(sql)
}
