package main


import (
	//"os"
	"fmt"
	"flag"
	"encoding/xml"
)

/*<script xmlns="http://www.daisy.org/ns/pipeline/data"*/
//id="dtbook-to-zedai"
//href="http://example.org/ws/scripts/dtbook-to-zedai">
//<nicename>DTBook to ZedAI</nicename>
//<description>Transforms DTBook XML into ZedAI XML.</description>
//<homepage>http://code.google.com/p/daisy-pipeline/wiki/DTBookToZedAI</homepage>
//<input
//desc="One or more DTBook files to be transformed. In the case of multiple files, a merge will be performed."
//mediaType="application/x-dtbook+xml" name="source" sequence="true"/>
//<option desc="Filename for the generated MODS file" mediaType="" name="opt-mods-filename"
//required="false" type="string" ordered="false" sequence="false"/>
//<option desc="Filename for the generated CSS file" mediaType="" name="opt-css-filename"
//required="false" type="string" ordered="false" sequence="false"/>
//<option desc="Filename for the generated ZedAI file" mediaType="" name="opt-zedai-filename"
//required="false" type="string" ordered="false" sequence="false"/>
//</script>

type Scripts struct{
	XMLName xml.Name `xml:"scripts"`
	Href string `xml:"href,attr"`
	Scripts []Script  `xml:"script"`
}
type Script struct{
	XMLName xml.Name `xml:"script"`
	Nicename string `xml:"nicename"`
	Id string `xml:"id",attr`
	Href string `xml:"href",attr`
	Description string `xml:"description"`
	Homepage string `xml:"homepage"`
	Inputs []Input;
	Options []Option;
}

type Input struct{
	XMLName xml.Name `xml:"input"`
	Desc string `xml:"desc,attr"`
	MediaType string `xml:"desc,attr"`
	Name string `xml:"name,attr"`
	Sequence bool `xml:"sequence,attr"`
}

type Option struct{
	XMLName xml.Name `xml:"option"`
	Desc string `xml:"desc,attr"`
	MediaType string `xml:"desc,attr"`
	Name string `xml:"name,attr"`
	Otype string `xml:"type,attr"`
	Sequence bool `xml:"sequence,attr"`
	Required bool `xml:"required,attr"`
	Ordered bool `xml:"ordered,attr"`
}

func (scripts *Scripts) String() string{
	str:=""
	for _,script := range scripts.Scripts {
		str+=script.String()+"\n"
	}
	return str
}
func (script *Script) String() string{
	return script.Nicename
}
func main() {
	println("Pipeline 2 client")

	// var command string
	var id string=""
	flag.StringVar(&id, "id","","The command you want to run")

	flag.Parse()

	var command = flag.Arg(0)
	if command==""{
		println("Error: command is empty")
		println("Usage: go-cli command")
	}

	if command == "scripts" {
		rs := get_scripts()
		fmt.Println(rs)
		//data:=new(Scripts)
		//fmt.Println(data)
		//xml.Unmarshal([]byte(rs),&data)
		//fmt.Println("scripts:")
		//fmt.Println(data)
	}else if command == "script" {
		if ( id == ""){
			println("Error: script needs an id")
			println("Usage: go-cli script --id script_id")
		}else{
			rs := get_script(id)
			fmt.Println(rs)
		}
	} else if command == "jobs" {
		rs := get_jobs()
		println(rs)

	}else if command == "job" {
		if ( id == ""){
			println("Error: job needs an id")
			println("Usage: go-cli job --id job_id")
		}else{
			rs := get_job(id)
			fmt.Println(rs)
		}
	}else if command == "log" {
		if ( id == ""){
			println("Error: log needs an id")
			println("Usage: go-cli log --id log_id")
		}else{
			rs := get_log(id)
			fmt.Println(rs)
		}
	}else if command == "result" {
		if ( id == ""){
			println("Error: result needs an id")
			println("Usage: go-cli result --id result_id")
		}else{
			rs := get_result(id)
			fmt.Println(rs)
		}
	}else if command == "delete" {
		if ( id == ""){
			println("Error: delete needs an id")
			println("Usage: go-cli delete --id result_id")
		}else{
			delete_job(id)
			fmt.Println("done")
		}
	}else if command == "alive" {
			rs := alive()
			fmt.Println(rs)
	}else if command == "halt" {
			rs := halt()
			fmt.Println(rs)
	}
}
