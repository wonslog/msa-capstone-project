<template>

<v-card style="width:300px; margin-left:5%;" outlined>
    <template slot="progress">
      <v-progress-linear
        color="deep-purple"
        height="10"
        indeterminate
      ></v-progress-linear>
    </template>

    <v-img
      style="width:290px; height:150px; border-radius:10px; position:relative; margin-left:5px; top:5px;"
      src="https://cdn.vuetifyjs.com/images/cards/cooking.png"
    ></v-img>

    <v-card-title v-if="value._links">
        Delivery # {{value._links.self.href.split("/")[value._links.self.href.split("/").length - 1]}}
    </v-card-title >
    <v-card-title v-else>
        Delivery
    </v-card-title >

    <v-card-text style = "margin-left:-15px; margin-top:10px;">

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="OrderId" v-model="value.orderId"/>
          </div>
          <div class="grey--text ml-4" v-else>
            OrderId :  {{value.orderId }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="OrderStatus" v-model="value.orderStatus"/>
          </div>
          <div class="grey--text ml-4" v-else>
            OrderStatus :  {{value.orderStatus }}
          </div>


          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="CustomerAddr" v-model="value.customerAddr"/>
          </div>
          <div class="grey--text ml-4" v-else>
            CustomerAddr :  {{value.customerAddr }}
          </div>


          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="CustomerTel" v-model="value.customerTel"/>
          </div>
          <div class="grey--text ml-4" v-else>
            CustomerTel :  {{value.customerTel }}
          </div>


          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="CustomerId" v-model="value.customerId"/>
          </div>
          <div class="grey--text ml-4" v-else>
            CustomerId :  {{value.customerId }}
          </div>


          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field type="number" label="DeliveryId" v-model="value.deliveryId"/>
          </div>
          <div class="grey--text ml-4" v-else>
            DeliveryId :  {{value.deliveryId }}
          </div>


    </v-card-text>

    <v-divider class="mx-4"></v-divider>

    <v-card-actions style = "position:absolute; right:0; bottom:0;">
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="edit"
        v-if="!editMode"
      >
        Edit
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="save"
        v-else
      >
        Save
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="remove"
        v-if="!editMode"
      >
        Delete
      </v-btn>
      
    </v-card-actions>
  </v-card>


</template>

<script>
  const axios = require('axios').default;
  
  
  
  
  
  
  
  export default {
    name: 'Delivery',
    components:{
    },
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        date: new Date().toISOString().substr(0, 10),
    }),
    created(){
    },

    methods: {
      edit(){
        this.editMode = true;
      },
      async save(){
        try{
          var temp = null;
          if(this.isNew){
            temp = await axios.post(axios.fixUrl('/deliveries'), this.value)
          }else{
            temp = await axios.put(axios.fixUrl(this.value._links.self.href), this.value)
          }

          this.value = temp.data;

          this.editMode = false;
          this.$emit('input', this.value);

          if(this.isNew){
            this.$emit('add', this.value);
          }else{
            this.$emit('edit', this.value);
          }

        }catch(e){
          alert(e.message)
        }
      },
      async remove(){
        try{
          await axios.delete(axios.fixUrl(this.value._links.self.href))
          this.editMode = false;
          this.isDeleted = true;

          this.$emit('input', this.value);
          this.$emit('delete', this.value);

        }catch(e){
          alert(e.message)
        }
      },
    },
  }
</script>

