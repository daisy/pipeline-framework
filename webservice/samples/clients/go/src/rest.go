package main

// Takes care of all the rest calls
import (
  "net/http"
  "io/ioutil"
)


func get_resource(uri string) string {
    // TODO authentication
    println("GET", uri)
    var authuri = uri
    resp, err := http.Get(authuri)
    if resp == nil || err != nil {
        println("GET failed for", uri)
        return ""
    }
    println("Response was ", resp.StatusCode)

    defer resp.Body.Close()
    body, _ := ioutil.ReadAll(resp.Body)
    return string(body)
}

func post_resource(uri string) {

}

func delete_resource(uri string) {
    var authuri = uri
    req, err := http.NewRequest("DELETE", authuri, nil)
    if err !=  nil {
        println ("DELETE failed for", uri)
        return
    }
    http.DefaultClient.Do(req)
}
