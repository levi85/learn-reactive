<template>
  <div>
    <v-file-input
      v-model="files"
      @change="selectFile"
      label="File input"
      multiple="true"
    ></v-file-input>
    <v-btn @click="uploadfile">
      Upload
    </v-btn>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      files: [],
      file: null,
      fileUrl: '',
    }
  },

  methods: {
    selectFile() {
      this.files.forEach((f) => {
        let reader = new FileReader()
        reader.onLoad = () => {
          this.fileUrl = reader.result
        }
        reader.readAsDataURL(f)
        console.log(reader)
      })
    },

    uploadfile() {
      let formData = new FormData()

      for (var x = 0; x < this.files.length; x++) {
        formData.append('files', this.files[x], this.files[x].name)
      }

      var Axios = axios.create({
        headers: {
          'Content-Type': 'multipart/form-data',
          accept: 'application/xml',
        },
        responseType: 'json',
      })

      Axios.post(`http://localhost:8081/v1/todos/upload`, formData)
        .then((response) => {
          if (response.status === 200) {
            alert('UPLOADED SUCCESS!')
          } else {
            alert('SOMETHING ABIT WRONG: ' + response.data)
          }
        })
        .catch((error) => {
          if (error.response.data) {
            alert(error.response.data.message)
          } else {
            alert(error.message)
          }
        })
    },
  },
}
</script>

<style></style>
