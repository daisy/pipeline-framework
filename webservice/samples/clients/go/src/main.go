package main


import (
    "flag"
)

var command = flag.String("command", "", "the command you want to run")

func main() {
    println("hello")

    
    flag.Parse()

    println(command)

    if &command == "scripts" {
    	rs := get_resource(SCRIPTS_URI)
    	println(rs)
    } else if &command == "jobs" {
    	rs := get_resource(JOBS_URI)
    	println(rs)
    }
}