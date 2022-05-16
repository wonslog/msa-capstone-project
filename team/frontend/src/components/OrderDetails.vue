<template>

<v-data-table
    :headers="headers"
    :items="orderDetails"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'OrderDetails',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "orderId", value: "orderId" },
            { text: "orderStatus", value: "orderStatus" },
            { text: "payStatus", value: "payStatus" },
            { text: "payAmount", value: "payAmount" },
            { text: "productName", value: "productName" },
            { text: "totalPrice", value: "totalPrice" },
        ],
        orderDetails : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/orderdetails')

      this.orderDetails = temp.data._embedded.orderdetails;

    },
    methods: {
    }
  }
</script>

