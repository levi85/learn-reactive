<template>
  <v-app>
    <upload></upload>
  </v-app>
</template>

<script>
import Upload from './components/Upload.vue'

export default {
  name: 'App',

  components: {
    Upload,
  },

  data: () => ({
    //
  }),
}
</script>
