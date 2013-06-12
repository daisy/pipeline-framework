package main

// Takes care of all the rest calls
import (
  "net/http"
  "io/ioutil"
)


func get_resource(uri string) string {
    // TODO authentication
    var authuri = uri
    resp, err := http.Get(authuri)
    println("Response was ", resp.StatusCode)

    if err != nil {
        // handle error
        println("GET failed for ", uri)
        return ""
    }

    defer resp.Body.Close()
    body, err := ioutil.ReadAll(resp.Body)
    return string(body)
}

func post_resource(uri string) {

}

func delete_resource(uri string) {
    var authuri = uri
    req, err := http.NewRequest("DELETE", authuri, nil)
    if err !=  nil {
        println ("DELETE failed for ", uri)
        return
    }
    http.DefaultClient.Do(req)
}
